
package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.InventoryBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.List;

public interface InventoryBatchRepository
        extends JpaRepository<InventoryBatch, Long> {

    List<InventoryBatch> findByMedicine_MedicineIdOrderByExpiryDateAsc(
            Long medicineId
    );

    // Original two-argument method used by InventoryService
    default List<InventoryBatch> findFefoCandidates(
            Long medicineId,
            LocalDate today) {

        return findFefoCandidates(
                medicineId,
                today,
                InventoryBatch.BatchStatus.ACTIVE
        );
    }

    // Three-argument method used internally
    @Query("""
        SELECT b FROM InventoryBatch b
        WHERE b.medicine.medicineId = :medicineId
        AND b.batchStatus = :status
        AND b.expiryDate >= :today
        AND b.quantityOnHand > (b.quantityReserved + b.quantityQuarantined)
        ORDER BY b.expiryDate ASC
        """)
    List<InventoryBatch> findFefoCandidates(
            @Param("medicineId") Long medicineId,
            @Param("today") LocalDate today,
            @Param("status") InventoryBatch.BatchStatus status
    );

    // Lock eligible batches while creating reservations
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT b FROM InventoryBatch b
        WHERE b.medicine.medicineId = :medicineId
        AND b.batchStatus = :status
        AND b.expiryDate >= :today
        AND b.quantityOnHand > (b.quantityReserved + b.quantityQuarantined)
        ORDER BY b.expiryDate ASC
        """)
    List<InventoryBatch> findFefoCandidatesForUpdate(
            @Param("medicineId") Long medicineId,
            @Param("today") LocalDate today,
            @Param("status") InventoryBatch.BatchStatus status
    );
}
