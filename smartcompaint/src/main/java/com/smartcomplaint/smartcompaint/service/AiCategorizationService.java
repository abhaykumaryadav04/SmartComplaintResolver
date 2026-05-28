package com.smartcomplaint.smartcompaint.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.smartcomplaint.smartcompaint.complaint.AiCategoryResponse;
import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;
import com.smartcomplaint.smartcompaint.enums.ComplaintPriority;
import com.smartcomplaint.smartcompaint.util.DepartmentResolver;

import java.util.Locale;
import java.util.Map;

@Service
public class AiCategorizationService {

    private final DepartmentResolver departmentResolver;
    private final String aiApiKey;

    public AiCategorizationService(DepartmentResolver departmentResolver, @Value("${app.ai.api-key:}") String aiApiKey) {
        this.departmentResolver = departmentResolver;
        this.aiApiKey = aiApiKey;
    }

    public AiCategoryResponse suggest(String title, String description, String location) {
        String text = (title + " " + description + " " + location).toLowerCase(Locale.ROOT);
        ComplaintCategory category = inferCategory(text);
        ComplaintPriority priority = inferPriority(text);
        double confidence = aiApiKey == null || aiApiKey.isBlank() ? 0.78 : 0.86;
        return new AiCategoryResponse(category, priority, departmentResolver.resolve(category), confidence);
    }

    private ComplaintCategory inferCategory(String text) {
        Map<ComplaintCategory, String[]> keywords = Map.of(
                ComplaintCategory.ROADS, new String[]{"road", "pothole", "traffic", "footpath", "sidewalk"},
                ComplaintCategory.WATER_SUPPLY, new String[]{"water", "leak", "pipe", "tap", "drinking"},
                ComplaintCategory.ELECTRICITY, new String[]{"electric", "power", "wire", "transformer", "voltage"},
                ComplaintCategory.SANITATION, new String[]{"garbage", "waste", "trash", "clean", "sanitation"},
                ComplaintCategory.STREET_LIGHT, new String[]{"streetlight", "street light", "lamp", "dark"},
                ComplaintCategory.DRAINAGE, new String[]{"drain", "sewer", "flood", "blocked"},
                ComplaintCategory.PUBLIC_SAFETY, new String[]{"unsafe", "danger", "accident", "crime", "broken"},
                ComplaintCategory.NOISE, new String[]{"noise", "loud", "speaker", "construction"}
        );

        return keywords.entrySet().stream()
                .filter(entry -> containsAny(text, entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(ComplaintCategory.OTHER);
    }

    private ComplaintPriority inferPriority(String text) {
        if (containsAny(text, "fire", "injury", "danger", "electric shock", "collapse", "sewage overflow")) {
            return ComplaintPriority.CRITICAL;
        }
        if (containsAny(text, "urgent", "accident", "flood", "unsafe", "blocked road", "no water")) {
            return ComplaintPriority.HIGH;
        }
        if (containsAny(text, "leak", "broken", "not working", "garbage", "dark")) {
            return ComplaintPriority.MEDIUM;
        }
        return ComplaintPriority.LOW;
    }

    private boolean containsAny(String text, String... values) {
        for (String value : values) {
            if (text.contains(value)) {
                return true;
            }
        }
        return false;
    }
}
