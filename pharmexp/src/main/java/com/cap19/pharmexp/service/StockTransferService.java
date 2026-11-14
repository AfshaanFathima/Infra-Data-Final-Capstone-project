
package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.StockTransferResponse;
import com.cap19.pharmexp.entity.*;
import com.cap19.pharmexp.exception.ResourceNotFoundException;
import com.cap19.pharmexp.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class StockTransferService {

    private final StockTransferRepository transferRepository;
    private final MedicineRepository medicineRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryBatchRepository batchRepository;
    private final ConsumptionRepository consumptionRepository;

    public StockTransferService(
            StockTransferRepository transferRepository,
            MedicineRepository medicineRepository,
            WarehouseRepository warehouseRepository,
            InventoryBatchRepository batchRepository,
            ConsumptionRepository consumptionRepository) {

        this.transferRepository = transferRepository;
        this.medicineRepository = medicineRepository;
        this.warehouseRepository = warehouseRepository;
        this.batchRepository = batchRepository;
        this.consumptionRepository = consumptionRepository;
    }

    // Retrieve all transfer recommendations
    @Transactional(readOnly = true)
    public List<StockTransferResponse> getAllTransfers() {
        return transferRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Retrieve transfers by status
    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByStatus(
            StockTransfer.TransferStatus status) {

        return transferRepository.findByStatus(status)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Retrieve transfers for a medicine
    @Transactional(readOnly = true)
    public List<StockTransferResponse> getTransfersByMedicine(
            Long medicineId) {

        if (!medicineRepository.existsById(medicineId)) {
            throw new ResourceNotFoundException(
                    "Medicine not found with ID: " + medicineId);
        }

        return transferRepository.findByMedicine_MedicineId(medicineId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Generate recommendations using the last 30 days of consumption
    @Transactional
    public List<StockTransferResponse> generateRecommendations(
            Long medicineId) {

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found with ID: " + medicineId));

        if (!Boolean.TRUE.equals(medicine.getIsActive())) {
            throw new IllegalArgumentException(
                    "Cannot generate transfers for an inactive medicine.");
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30);

        List<Warehouse> warehouses = warehouseRepository.findAll()
                .stream()
                .filter(w -> Boolean.TRUE.equals(w.getIsActive()))
                .toList();

        if (warehouses.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two active warehouses are required.");
        }

        // Calculate usable stock in each warehouse.
        // Expired, quarantined, recalled and depleted stock is excluded.
        Map<Long, Integer> availableStock = new HashMap<>();

        for (Warehouse warehouse : warehouses) {
            int available = 0;

            for (InventoryBatch batch : batchRepository.findAll()) {

                if (batch.getMedicine() == null ||
                    batch.getWarehouse() == null) {
                    continue;
                }

                boolean correctMedicine =
                        batch.getMedicine().getMedicineId().equals(medicineId);

                boolean correctWarehouse =
                        batch.getWarehouse().getWarehouseId()
                                .equals(warehouse.getWarehouseId());

                boolean usableBatch =
                        batch.getBatchStatus() ==
                                InventoryBatch.BatchStatus.ACTIVE
                        && batch.getExpiryDate() != null
                        && !batch.getExpiryDate().isBefore(today);

                if (correctMedicine && correctWarehouse && usableBatch) {
                    int onHand = batch.getQuantityOnHand() == null
                            ? 0 : batch.getQuantityOnHand();

                    int reserved = batch.getQuantityReserved() == null
                            ? 0 : batch.getQuantityReserved();

                    int quarantined = batch.getQuantityQuarantined() == null
                            ? 0 : batch.getQuantityQuarantined();

                    available += Math.max(
                            0, onHand - reserved - quarantined);
                }
            }

            availableStock.put(warehouse.getWarehouseId(), available);
        }

        // Calculate consumption for the previous 30 days.
        Map<Long, Integer> recentDemand = new HashMap<>();

        List<MedicineConsumption> consumptionRecords =
                consumptionRepository.findByMedicine_MedicineId(medicineId);

        for (MedicineConsumption record : consumptionRecords) {

            if (record.getConsumptionDate() == null ||
                record.getWarehouse() == null) {
                continue;
            }

            LocalDate consumptionDate = record.getConsumptionDate();

            if (!consumptionDate.isBefore(startDate)
                    && !consumptionDate.isAfter(today)) {

                Long warehouseId =
                        record.getWarehouse().getWarehouseId();

                int quantity = record.getConsumedQuantity() == null
                        ? 0 : record.getConsumedQuantity();

                recentDemand.merge(warehouseId, quantity, Integer::sum);
            }
        }

        // A warehouse's 30-day consumption is its target stock.
        Map<Long, Integer> surplus = new HashMap<>();
        Map<Long, Integer> shortage = new HashMap<>();

        for (Warehouse warehouse : warehouses) {

            Long warehouseId = warehouse.getWarehouseId();

            int stock = availableStock.getOrDefault(warehouseId, 0);
            int demand = recentDemand.getOrDefault(warehouseId, 0);

            surplus.put(warehouseId, Math.max(0, stock - demand));
            shortage.put(warehouseId, Math.max(0, demand - stock));
        }

        List<StockTransferResponse> recommendations =
                new ArrayList<>();

        // Match surplus warehouses with shortage warehouses.
        for (Warehouse source : warehouses) {

            Long sourceId = source.getWarehouseId();
            int remainingSurplus = surplus.getOrDefault(sourceId, 0);

            if (remainingSurplus <= 0) {
                continue;
            }

            for (Warehouse destination : warehouses) {

                Long destinationId = destination.getWarehouseId();

                if (sourceId.equals(destinationId)) {
                    continue;
                }

                int remainingShortage =
                        shortage.getOrDefault(destinationId, 0);

                if (remainingShortage <= 0 || remainingSurplus <= 0) {
                    continue;
                }

                int transferQuantity = Math.min(
                        remainingSurplus, remainingShortage);

                if (transferQuantity <= 0) {
                    continue;
                }

                StockTransfer transfer = new StockTransfer();
                transfer.setMedicine(medicine);
                transfer.setSourceWarehouse(source);
                transfer.setDestinationWarehouse(destination);
                transfer.setSuggestedQuantity(transferQuantity);
                transfer.setStatus(
                        StockTransfer.TransferStatus.RECOMMENDED);
                transfer.setRecommendationDate(today);
                transfer.setTransferReason(
                        "Source warehouse has surplus stock compared " +
                        "with its last 30-day consumption, while the " +
                        "destination warehouse has a shortage.");

                StockTransfer saved =
                        transferRepository.save(transfer);

                recommendations.add(convertToResponse(saved));

                remainingSurplus -= transferQuantity;
                remainingShortage -= transferQuantity;

                surplus.put(sourceId, remainingSurplus);
                shortage.put(destinationId, remainingShortage);
            }
        }

        return recommendations;
    }

    // Approve or reject a recommendation.
    // This does not change inventory quantities.
    @Transactional
    public StockTransferResponse reviewTransfer(
            Long transferId,
            StockTransfer.TransferStatus newStatus,
            String reviewedBy) {

        StockTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transfer not found with ID: " + transferId));

        if (transfer.getStatus() !=
                StockTransfer.TransferStatus.RECOMMENDED) {
            throw new IllegalArgumentException(
                    "Only recommended transfers can be reviewed.");
        }

        if (newStatus != StockTransfer.TransferStatus.APPROVED
                && newStatus != StockTransfer.TransferStatus.REJECTED) {
            throw new IllegalArgumentException(
                    "Review status must be APPROVED or REJECTED.");
        }

        if (reviewedBy == null || reviewedBy.isBlank()) {
            throw new IllegalArgumentException(
                    "Reviewer name is required.");
        }

        transfer.setStatus(newStatus);
        transfer.setReviewedBy(reviewedBy);
        transfer.setReviewedAt(LocalDateTime.now());

        return convertToResponse(transferRepository.save(transfer));
    }

    // Convert entity to response DTO
    private StockTransferResponse convertToResponse(
            StockTransfer transfer) {

        StockTransferResponse response =
                new StockTransferResponse();

        response.setTransferId(transfer.getTransferId());

        response.setMedicineId(
                transfer.getMedicine().getMedicineId());

        response.setMedicineName(
                transfer.getMedicine().getGenericName());

        response.setSourceWarehouseId(
                transfer.getSourceWarehouse().getWarehouseId());

        response.setSourceWarehouseName(
                transfer.getSourceWarehouse().getWarehouseName());

        response.setDestinationWarehouseId(
                transfer.getDestinationWarehouse().getWarehouseId());

        response.setDestinationWarehouseName(
                transfer.getDestinationWarehouse().getWarehouseName());

        response.setSuggestedQuantity(
                transfer.getSuggestedQuantity());

        response.setTransferReason(transfer.getTransferReason());
        response.setStatus(transfer.getStatus().name());
        response.setReviewedBy(transfer.getReviewedBy());
        response.setReviewedAt(transfer.getReviewedAt());
        response.setRecommendationDate(
                transfer.getRecommendationDate());

        return response;
    }
}
