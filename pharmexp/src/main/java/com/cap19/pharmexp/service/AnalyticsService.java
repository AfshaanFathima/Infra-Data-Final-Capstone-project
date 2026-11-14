
package com.cap19.pharmexp.service;

import com.cap19.pharmexp.entity.*;
import com.cap19.pharmexp.repository.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final MedicineRepository medicineRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryBatchRepository inventoryBatchRepository;
    private final ReservationRepository reservationRepository;
    private final StockTransferRepository stockTransferRepository;
    private final ConsumptionRepository consumptionRepository;

    public AnalyticsService(
            MedicineRepository medicineRepository,
            WarehouseRepository warehouseRepository,
            InventoryBatchRepository inventoryBatchRepository,
            ReservationRepository reservationRepository,
            StockTransferRepository stockTransferRepository,
            ConsumptionRepository consumptionRepository) {

        this.medicineRepository = medicineRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryBatchRepository = inventoryBatchRepository;
        this.reservationRepository = reservationRepository;
        this.stockTransferRepository = stockTransferRepository;
        this.consumptionRepository = consumptionRepository;
    }

    // 1. Main dashboard summary
    public Map<String, Object> getDashboardSummary() {

        LocalDate today = LocalDate.now();
        LocalDate nearExpiryDate = today.plusDays(90);

        List<InventoryBatch> batches = inventoryBatchRepository.findAll();

        long availableUnits = 0;
        long reservedUnits = 0;
        long quarantinedUnits = 0;
        long expiredBatches = 0;
        long nearExpiryBatches = 0;
        long expiredUnits = 0;

        BigDecimal expiryLoss = BigDecimal.ZERO;

        for (InventoryBatch batch : batches) {

            int onHand = batch.getQuantityOnHand();
            int reserved = batch.getQuantityReserved();
            int quarantined = batch.getQuantityQuarantined();

            reservedUnits += reserved;
            quarantinedUnits += quarantined;

            if (batch.getExpiryDate().isBefore(today)) {
                expiredBatches++;
                expiredUnits += onHand;

                if (batch.getUnitCost() != null) {
                    expiryLoss = expiryLoss.add(
                        batch.getUnitCost().multiply(BigDecimal.valueOf(onHand))
                    );
                }
            } else if (
                !batch.getExpiryDate().isAfter(nearExpiryDate)
                && batch.getBatchStatus() == InventoryBatch.BatchStatus.ACTIVE
            ) {
                nearExpiryBatches++;
            }

            if (
                batch.getBatchStatus() == InventoryBatch.BatchStatus.ACTIVE
                && !batch.getExpiryDate().isBefore(today)
            ) {
                availableUnits += Math.max(0, onHand - reserved - quarantined);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalMedicines", medicineRepository.count());
        result.put("totalWarehouses", warehouseRepository.count());
        result.put("totalInventoryBatches", batches.size());
        result.put("totalAvailableUnits", availableUnits);
        result.put("totalReservedUnits", reservedUnits);
        result.put("totalQuarantinedUnits", quarantinedUnits);
        result.put("expiredBatches", expiredBatches);
        result.put("nearExpiryBatches", nearExpiryBatches);
        result.put("expiredUnits", expiredUnits);
        result.put("estimatedExpiryLoss", expiryLoss);
        result.put("pendingReservations",
            reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.PENDING)
                .count());
        result.put("pendingTransfers",
            stockTransferRepository.findAll().stream()
                .filter(t -> t.getStatus() == StockTransfer.TransferStatus.RECOMMENDED)
                .count());

        return result;
    }

    // 2. Expiry analytics
    public Map<String, Object> getExpiryAnalytics() {

        LocalDate today = LocalDate.now();
        LocalDate nearExpiryDate = today.plusDays(90);

        List<InventoryBatch> batches = inventoryBatchRepository.findAll();

        List<Map<String, Object>> expired = new ArrayList<>();
        List<Map<String, Object>> nearExpiry = new ArrayList<>();

        BigDecimal expiryLoss = BigDecimal.ZERO;

        for (InventoryBatch batch : batches) {

            if (batch.getExpiryDate().isBefore(today)) {

                BigDecimal loss = batch.getUnitCost() == null
                    ? BigDecimal.ZERO
                    : batch.getUnitCost().multiply(
                        BigDecimal.valueOf(batch.getQuantityOnHand())
                    );

                expiryLoss = expiryLoss.add(loss);

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("batchId", batch.getBatchId());
                item.put("batchNumber", batch.getBatchNumber());
                item.put("medicine", batch.getMedicine().getGenericName());
                item.put("expiryDate", batch.getExpiryDate());
                item.put("quantityOnHand", batch.getQuantityOnHand());
                item.put("estimatedLoss", loss);

                expired.add(item);

            } else if (
                !batch.getExpiryDate().isAfter(nearExpiryDate)
                && batch.getBatchStatus() == InventoryBatch.BatchStatus.ACTIVE
            ) {

                Map<String, Object> item = new LinkedHashMap<>();
                item.put("batchId", batch.getBatchId());
                item.put("batchNumber", batch.getBatchNumber());
                item.put("medicine", batch.getMedicine().getGenericName());
                item.put("expiryDate", batch.getExpiryDate());
                item.put("quantityOnHand", batch.getQuantityOnHand());

                nearExpiry.add(item);
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("expiredBatchCount", expired.size());
        result.put("nearExpiryBatchCount", nearExpiry.size());
        result.put("estimatedExpiryLoss", expiryLoss);
        result.put("expiredBatches", expired);
        result.put("nearExpiryBatches", nearExpiry);

        return result;
    }

    // 3. Reservation analytics
    public Map<String, Object> getReservationAnalytics() {

        List<Reservation> reservations = reservationRepository.findAll();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalReservations", reservations.size());
        result.put("pending", countReservations(reservations,
            Reservation.ReservationStatus.PENDING));
        result.put("confirmed", countReservations(reservations,
            Reservation.ReservationStatus.CONFIRMED));
        result.put("cancelled", countReservations(reservations,
            Reservation.ReservationStatus.CANCELLED));
        result.put("expired", countReservations(reservations,
            Reservation.ReservationStatus.EXPIRED));

        return result;
    }

    private long countReservations(
            List<Reservation> reservations,
            Reservation.ReservationStatus status) {

        return reservations.stream()
            .filter(r -> r.getStatus() == status)
            .count();
    }

    // 4. Stock transfer analytics
    public Map<String, Object> getTransferAnalytics() {

        List<StockTransfer> transfers = stockTransferRepository.findAll();

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalTransfers", transfers.size());
        result.put("recommended", countTransfers(transfers,
            StockTransfer.TransferStatus.RECOMMENDED));
        result.put("approved", countTransfers(transfers,
            StockTransfer.TransferStatus.APPROVED));
        result.put("rejected", countTransfers(transfers,
            StockTransfer.TransferStatus.REJECTED));
        result.put("cancelled", countTransfers(transfers,
            StockTransfer.TransferStatus.CANCELLED));

        return result;
    }

    private long countTransfers(
            List<StockTransfer> transfers,
            StockTransfer.TransferStatus status) {

        return transfers.stream()
            .filter(t -> t.getStatus() == status)
            .count();
    }

    // 5. Consumption analytics grouped by medicine and warehouse
    public Map<String, Object> getConsumptionAnalytics() {

        List<MedicineConsumption> records =
            consumptionRepository.findAll();

        Map<String, Long> consumptionTotals = new LinkedHashMap<>();

        for (MedicineConsumption record : records) {

            String medicine = record.getMedicine().getGenericName();
            String warehouse = record.getWarehouse().getWarehouseName();

            String key = medicine + " - " + warehouse;

            consumptionTotals.merge(
                key,
                (long) record.getConsumedQuantity(),
                Long::sum
            );
        }

        List<Map<String, Object>> summary = new ArrayList<>();

        for (Map.Entry<String, Long> entry : consumptionTotals.entrySet()) {

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("medicineAndWarehouse", entry.getKey());
            item.put("totalConsumedQuantity", entry.getValue());

            summary.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalConsumptionRecords", records.size());
        result.put("consumptionSummary", summary);

        return result;
    }
}
