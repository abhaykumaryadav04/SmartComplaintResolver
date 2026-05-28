package com.smartcomplaint.smartcompaint.complaint;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import com.smartcomplaint.smartcompaint.enums.ComplaintCategory;

import io.swagger.v3.oas.annotations.media.Schema;

public record ComplaintCreateRequest(
        @NotBlank @Size(min = 5, max = 150) String title,
        @NotBlank @Size(min = 20, max = 4000) String description,
        ComplaintCategory category,
        @NotBlank @Size(max = 255) String location,
        @Schema(type = "string", format = "binary") MultipartFile image
) {
}
