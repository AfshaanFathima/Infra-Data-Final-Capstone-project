package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.InventoryBatchRequest;
import com.cap19.pharmexp.dto.InventoryBatchResponse;
import com.cap19.pharmexp.dto.InventorySummaryResponse;
import com.cap19.pharmexp.entity.InventoryBatch;
import com.cap19.pharmexp.entity.Medicine;
import com.cap19.pharmexp.entity.Warehouse;
import com.cap19.pharmexp.repository.InventoryBatchRepository;
import com.cap19.pharmexp.repository.MedicineRepository;
import com.cap19.pharmexp.repository.WarehouseRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private static final long NEAR_EXPIRY_DAYS = 90;

    private final InventoryBatchRepository inventoryBatchRepository;
    private final MedicineRepository medicineRepository;
    private final WarehouseRepository warehouseRepository;

    public InventoryService(
            InventoryBatchRepository inventoryBatchRepository,
            MedicineRepository medicineRepository,
            WarehouseRepository warehouseRepository) {

        this.inventoryBatchRepository = inventoryBatchRepository;
        this.medicineRepository = medicineRepository;
        this.warehouseRepository = warehouseRepository;
    }

    public InventoryBatchResponse createBatch(InventoryBatchRequest request) {

        validateRequest(request);

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found"));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() ->
                        new RuntimeException("Warehouse not found"));

        if (!Boolean.TRUE.equals(medicine.getIsActive())) {
            throw new IllegalArgumentException("Cannot add stock for an inactive medicine");
        }

        if (!Boolean.TRUE.equals(warehouse.getIsActive())) {
            throw new IllegalArgumentException("Cannot add stock to an inactive warehouse");
        }

        InventoryBatch batch = new InventoryBatch();

        batch.setMedicine(medicine);
        batch.setWarehouse(warehouse);
        batch.setBatchNumber(request.getBatchNumber());
        batch.setManufacturingDate(request.getManufacturingDate());
        batch.setExpiryDate(request.getExpiryDate());
        batch.setReceivedQuantity(request.getReceivedQuantity());
        batch.setQuantityOnHand(request.getQuantityOnHand());
        batch.setQuantityReserved(0);
        batch.setQuantityQuarantined(0);
        batch.setUnitCost(request.getUnitCost());
        batch.setBatchStatus(InventoryBatch.BatchStatus.ACTIVE);

        InventoryBatch savedBatch = inventoryBatchRepository.save(batch);

        return convertToResponse(savedBatch);
    }

    @Transactional(readOnly = true)
    public List<InventoryBatchResponse> getAllBatches() {

        return inventoryBatchRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventoryBatchResponse getBatchById(Long id) {

        InventoryBatch batch = inventoryBatchRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Inventory batch not found: " + id));

        return convertToResponse(batch);
    }

    @Transactional(readOnly = true)
    public List<InventoryBatchResponse> getBatchesByMedicine(Long medicineId) {

        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Medicine not found: " + medicineId);
        }

        return inventoryBatchRepository
                .findByMedicine_MedicineIdOrderByExpiryDateAsc(medicineId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryBatchResponse> getFefoBatches(Long medicineId) {

        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Medicine not found: " + medicineId);
        }

        return inventoryBatchRepository
                .findFefoCandidates(medicineId, LocalDate.now())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public InventorySummaryResponse getInventorySummary() {

        List<InventoryBatch> batches = inventoryBatchRepository.findAll();

        LocalDate today = LocalDate.now();

        long totalUnits = 0;
        long availableUnits = 0;
        long expired = 0;
        long nearExpiry = 0;
        long quarantined = 0;

        for (InventoryBatch batch : batches) {

            int onHand = safeValue(batch.getQuantityOnHand());
            int reserved = safeValue(batch.getQuantityReserved());
            int quarantinedQuantity = safeValue(batch.getQuantityQuarantined());

            totalUnits += onHand;

            if (batch.getExpiryDate().isBefore(today)) {
                expired++;
            } else if (!batch.getExpiryDate().isAfter(
                    today.plusDays(NEAR_EXPIRY_DAYS))) {
                nearExpiry++;
            }

            if (batch.getBatchStatus() ==
                    InventoryBatch.BatchStatus.QUARANTINED) {
                quarantined++;
            }

            if (batch.getBatchStatus() ==
                    InventoryBatch.BatchStatus.ACTIVE
                    && !batch.getExpiryDate().isBefore(today)) {

                availableUnits += Math.max(
                        0, onHand - reserved - quarantinedQuantity
                );
            }
        }

        InventorySummaryResponse response = new InventorySummaryResponse();

        response.setTotalBatches(batches.size());
        response.setTotalUnitsOnHand(totalUnits);
        response.setTotalAvailableUnits(availableUnits);
        response.setExpiredBatches(expired);
        response.setNearExpiryBatches(nearExpiry);
        response.setQuarantinedBatches(quarantined);

        return response;
    }

    private void validateRequest(InventoryBatchRequest request) {

        if (request.getExpiryDate() == null) {
            throw new IllegalArgumentException("Expiry date is required");
        }

        if (request.getReceivedQuantity() == null
                || request.getReceivedQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Received quantity must be greater than zero");
        }

        if (request.getQuantityOnHand() == null
                || request.getQuantityOnHand() < 0) {
            throw new IllegalArgumentException(
                    "Quantity on hand cannot be negative");
        }

        if (request.getQuantityOnHand() > request.getReceivedQuantity()) {
            throw new IllegalArgumentException(
                    "Quantity on hand cannot exceed received quantity");
        }

        if (request.getManufacturingDate() != null
                && request.getManufacturingDate().isAfter(request.getExpiryDate())) {
            throw new IllegalArgumentException(
                    "Manufacturing date cannot be after expiry date");
        }

        if (request.getUnitCost() != null
                && request.getUnitCost().signum() < 0) {
            throw new IllegalArgumentException(
                    "Unit cost cannot be negative");
        }
    }

    private int safeValue(Integer value) {
        return value == null ? 0 : value;
    }

    private InventoryBatchResponse convertToResponse(InventoryBatch batch) {

        LocalDate today = LocalDate.now();
        LocalDate expiryDate = batch.getExpiryDate();

        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

        String expiryStatus;

        if (daysUntilExpiry < 0) {
            expiryStatus = "EXPIRED";
        } else if (daysUntilExpiry <= NEAR_EXPIRY_DAYS) {
            expiryStatus = "NEAR_EXPIRY";
        } else {
            expiryStatus = "SAFE";
        }

        int onHand = safeValue(batch.getQuantityOnHand());
        int reserved = safeValue(batch.getQuantityReserved());
        int quarantined = safeValue(batch.getQuantityQuarantined());

        int available = 0;

        if (batch.getBatchStatus() == InventoryBatch.BatchStatus.ACTIVE
                && daysUntilExpiry >= 0) {
            available = Math.max(0, onHand - reserved - quarantined);
        }

        InventoryBatchResponse response = new InventoryBatchResponse();

        response.setBatchId(batch.getBatchId());
        response.setMedicineId(batch.getMedicine().getMedicineId());
        response.setMedicineName(batch.getMedicine().getGenericName());
        response.setWarehouseId(batch.getWarehouse().getWarehouseId());
        response.setWarehouseName(batch.getWarehouse().getWarehouseName());
        response.setBatchNumber(batch.getBatchNumber());
        response.setManufacturingDate(batch.getManufacturingDate());
        response.setExpiryDate(expiryDate);
        response.setReceivedQuantity(batch.getReceivedQuantity());
        response.setQuantityOnHand(onHand);
        response.setQuantityReserved(reserved);
        response.setQuantityQuarantined(quarantined);
        response.setAvailableQuantity(available);
        response.setUnitCost(batch.getUnitCost());
        response.setBatchStatus(batch.getBatchStatus().name());
        response.setExpiryStatus(expiryStatus);
        response.setDaysUntilExpiry(daysUntilExpiry);

        return response;
    }
}