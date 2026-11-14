
package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.ApiResponse;
import com.cap19.pharmexp.dto.SubstituteRequest;
import com.cap19.pharmexp.dto.SubstituteResponse;
import com.cap19.pharmexp.service.SubstituteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/substitutes")
public class SubstituteController {

    private final SubstituteService substituteService;

    public SubstituteController(SubstituteService substituteService) {
        this.substituteService = substituteService;
    }

    @GetMapping
    public ApiResponse<List<SubstituteResponse>> getAllSubstitutes() {
        return new ApiResponse<>(
                true,
                "Substitutions retrieved successfully.",
                substituteService.getAllSubstitutes()
        );
    }

    @GetMapping("/medicine/{medicineId}")
    public ApiResponse<List<SubstituteResponse>> getSubstitutesForMedicine(
            @PathVariable Long medicineId) {
        return new ApiResponse<>(
                true,
                "Substitutions retrieved successfully.",
                substituteService.getSubstitutesForMedicine(medicineId)
        );
    }

    @GetMapping("/approved/{medicineId}")
    public ApiResponse<List<SubstituteResponse>> getApprovedSubstitutes(
            @PathVariable Long medicineId) {
        return new ApiResponse<>(
                true,
                "Approved substitutes retrieved successfully.",
                substituteService.getApprovedSubstitutes(medicineId)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SubstituteResponse> createSubstitute(
            @Valid @RequestBody SubstituteRequest request) {
        return new ApiResponse<>(
                true,
                "Substitution request created with PENDING approval.",
                substituteService.createSubstitute(request)
        );
    }

    @PutMapping("/{id}/approval")
    public ApiResponse<SubstituteResponse> updateApproval(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String approvedBy) {
        return new ApiResponse<>(
                true,
                "Substitution approval status updated.",
                substituteService.updateApproval(id, status, approvedBy)
        );
    }
}
