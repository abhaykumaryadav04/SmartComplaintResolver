package com.smartcomplaint.smartcompaint.util;


import org.springframework.stereotype.Component;

import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;

import java.util.EnumMap;
import java.util.Map;

@Component
public class DepartmentResolver {

    private final Map<ComplaintCategory, String> departments = new EnumMap<>(ComplaintCategory.class);

    public DepartmentResolver() {
        departments.put(ComplaintCategory.ROADS, "Public Works Department");
        departments.put(ComplaintCategory.WATER_SUPPLY, "Water Department");
        departments.put(ComplaintCategory.ELECTRICITY, "Electricity Board");
        departments.put(ComplaintCategory.SANITATION, "Sanitation Department");
        departments.put(ComplaintCategory.STREET_LIGHT, "Electrical Maintenance");
        departments.put(ComplaintCategory.DRAINAGE, "Drainage Department");
        departments.put(ComplaintCategory.PUBLIC_SAFETY, "Public Safety Department");
        departments.put(ComplaintCategory.NOISE, "Municipal Enforcement");
        departments.put(ComplaintCategory.OTHER, "General Administration");
    }

    public String resolve(ComplaintCategory category) {
        return departments.getOrDefault(category, "General Administration");
    }
}
