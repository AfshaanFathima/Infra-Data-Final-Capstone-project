package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.ApiResponse;
import com.cap19.pharmexp.dto.WarehouseRequest;
import com.cap19.pharmexp.dto.WarehouseResponse;
import com.cap19.pharmexp.service.WarehouseService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Warehouses retrieved successfully",
                        warehouseService.getAllWarehouses()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouseById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Warehouse retrieved successfully",
                        warehouseService.getWarehouseById(id)
                )
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseRequest request) {

        WarehouseResponse response = warehouseService.createWarehouse(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ApiResponse<>(
                        true,
                        "Warehouse created successfully",
                        response
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> updateWarehouse(
            @PathVariable Long id,
            @Valid @RequestBody WarehouseRequest request) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Warehouse updated successfully",
                        warehouseService.updateWarehouse(id, request)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateWarehouse(
            @PathVariable Long id) {

        warehouseService.deactivateWarehouse(id);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Warehouse deactivated successfully",
                        null
                )
        );
    }
}