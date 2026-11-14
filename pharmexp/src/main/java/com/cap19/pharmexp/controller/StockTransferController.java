
package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.StockTransferResponse;
import com.cap19.pharmexp.entity.StockTransfer;
import com.cap19.pharmexp.service.StockTransferService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transfers")
public class StockTransferController {

    private final StockTransferService transferService;

    public StockTransferController(
            StockTransferService transferService) {
        this.transferService = transferService;
    }

    // Get all transfer recommendations
    @GetMapping
    public ResponseEntity<List<StockTransferResponse>> getAllTransfers() {
        return ResponseEntity.ok(
                transferService.getAllTransfers());
    }

    // Get transfers by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<StockTransferResponse>> getByStatus(
            @PathVariable StockTransfer.TransferStatus status) {

        return ResponseEntity.ok(
                transferService.getTransfersByStatus(status));
    }

    // Get transfers for a particular medicine
    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<StockTransferResponse>> getByMedicine(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                transferService.getTransfersByMedicine(medicineId));
    }

    // Generate recommendations for a medicine
    @PostMapping("/recommendations/{medicineId}")
    public ResponseEntity<List<StockTransferResponse>>
            generateRecommendations(
                    @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                transferService.generateRecommendations(medicineId));
    }

    // Approve or reject a recommendation
    @PutMapping("/{transferId}/review")
    public ResponseEntity<StockTransferResponse> reviewTransfer(
            @PathVariable Long transferId,
            @RequestParam StockTransfer.TransferStatus status,
            @RequestParam String reviewedBy) {

        return ResponseEntity.ok(
                transferService.reviewTransfer(
                        transferId, status, reviewedBy));
    }
}
