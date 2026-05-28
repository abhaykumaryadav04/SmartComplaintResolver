package com.smartcomplaint.smartcompaint.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartcomplaint.smartcompaint.analytics.AnalyticsResponse;
import com.smartcomplaint.smartcompaint.service.AnalyticsService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin")
public class AdminController {

    private final AnalyticsService analyticsService;

    public AdminController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/analytics")
    public AnalyticsResponse analytics() {
        return analyticsService.dashboard();
    }
}
