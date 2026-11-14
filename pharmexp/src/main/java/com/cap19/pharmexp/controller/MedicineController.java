package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.ApiResponse;
import com.cap19.pharmexp.dto.MedicineRequest;
import com.cap19.pharmexp.dto.MedicineResponse;
import com.cap19.pharmexp.service.MedicineService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MedicineResponse>>> getAllMedicines() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Medicines retrieved successfully",
                        medicineService.getAllMedicines())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineResponse>> getMedicineById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Medicine retrieved successfully",
                        medicineService.getMedicineById(id))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MedicineResponse>> createMedicine(
            @Valid @RequestBody MedicineRequest request) {

        MedicineResponse response = medicineService.createMedicine(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(true, "Medicine created successfully", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MedicineResponse>> updateMedicine(
            @PathVariable Long id,
            @Valid @RequestBody MedicineRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Medicine updated successfully",
                        medicineService.updateMedicine(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMedicine(
            @PathVariable Long id) {

        medicineService.deleteMedicine(id);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Medicine deleted successfully", null)
        );
    }
}