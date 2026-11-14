package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.ApiResponse;
import com.cap19.pharmexp.dto.InventoryBatchRequest;
import com.cap19.pharmexp.dto.InventoryBatchResponse;
import com.cap19.pharmexp.dto.InventorySummaryResponse;
import com.cap19.pharmexp.service.InventoryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/batches")
    public ResponseEntity<ApiResponse<InventoryBatchResponse>> createBatch(
            @Valid @RequestBody InventoryBatchRequest request) {

        InventoryBatchResponse response = inventoryService.createBatch(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Inventory batch created successfully",
                        response
                )
        );
    }

    @GetMapping("/batches")
    public ResponseEntity<ApiResponse<List<InventoryBatchResponse>>> getAllBatches() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Inventory batches retrieved successfully",
                        inventoryService.getAllBatches()
                )
        );
    }

    @GetMapping("/batches/{id}")
    public ResponseEntity<ApiResponse<InventoryBatchResponse>> getBatchById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Inventory batch retrieved successfully",
                        inventoryService.getBatchById(id)
                )
        );
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<ApiResponse<List<InventoryBatchResponse>>> getBatchesByMedicine(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Medicine inventory retrieved successfully",
                        inventoryService.getBatchesByMedicine(medicineId)
                )
        );
    }

    @GetMapping("/fefo/{medicineId}")
    public ResponseEntity<ApiResponse<List<InventoryBatchResponse>>> getFefoBatches(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Eligible batches retrieved in FEFO order",
                        inventoryService.getFefoBatches(medicineId)
                )
        );
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<InventorySummaryResponse>> getInventorySummary() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Inventory summary retrieved successfully",
                        inventoryService.getInventorySummary()
                )
        );
    }
}