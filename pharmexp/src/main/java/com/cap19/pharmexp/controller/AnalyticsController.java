
package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.service.AnalyticsService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    // Main dashboard
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard() {
        return analyticsService.getDashboardSummary();
    }

    // Expiry analysis
    @GetMapping("/expiry")
    public Map<String, Object> getExpiryAnalytics() {
        return analyticsService.getExpiryAnalytics();
    }

    // Reservation analysis
    @GetMapping("/reservations")
    public Map<String, Object> getReservationAnalytics() {
        return analyticsService.getReservationAnalytics();
    }

    // Stock transfer analysis
    @GetMapping("/transfers")
    public Map<String, Object> getTransferAnalytics() {
        return analyticsService.getTransferAnalytics();
    }

    // Medicine consumption analysis
    @GetMapping("/consumption")
    public Map<String, Object> getConsumptionAnalytics() {
        return analyticsService.getConsumptionAnalytics();
    }
}
