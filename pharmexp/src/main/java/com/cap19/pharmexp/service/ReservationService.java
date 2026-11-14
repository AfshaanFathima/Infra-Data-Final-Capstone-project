
package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.ReservationAllocationResponse;
import com.cap19.pharmexp.dto.ReservationRequest;
import com.cap19.pharmexp.dto.ReservationResponse;
import com.cap19.pharmexp.entity.InventoryBatch;
import com.cap19.pharmexp.entity.Medicine;
import com.cap19.pharmexp.entity.Reservation;
import com.cap19.pharmexp.entity.ReservationAllocation;
import com.cap19.pharmexp.repository.InventoryBatchRepository;
import com.cap19.pharmexp.repository.MedicineRepository;
import com.cap19.pharmexp.repository.ReservationAllocationRepository;
import com.cap19.pharmexp.repository.ReservationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationAllocationRepository allocationRepository;
    private final InventoryBatchRepository batchRepository;
    private final MedicineRepository medicineRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationAllocationRepository allocationRepository,
            InventoryBatchRepository batchRepository,
            MedicineRepository medicineRepository) {
        this.reservationRepository = reservationRepository;
        this.allocationRepository = allocationRepository;
        this.batchRepository = batchRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = findReservation(id);
        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {

        LocalDateTime now = LocalDateTime.now();

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() ->
                        new RuntimeException("Medicine not found."));

        if (!Boolean.TRUE.equals(medicine.getIsActive())) {
            throw new IllegalArgumentException(
                    "Cannot reserve an inactive medicine."
            );
        }

        LocalDateTime expiryTime = request.getExpiresAt() == null
                ? now.plusMinutes(30)
                : request.getExpiresAt();

        if (!expiryTime.isAfter(now)) {
            throw new IllegalArgumentException(
                    "Reservation expiry time must be in the future."
            );
        }

        List<InventoryBatch> batches =
                batchRepository.findFefoCandidatesForUpdate(
                        medicine.getMedicineId(),
                        LocalDate.now(),
                        InventoryBatch.BatchStatus.ACTIVE
                );

        int requested = request.getRequestedQuantity();
        int totalAvailable = 0;

        for (InventoryBatch batch : batches) {
            int available = batch.getQuantityOnHand()
                    - batch.getQuantityReserved()
                    - batch.getQuantityQuarantined();

            totalAvailable += Math.max(available, 0);
        }

        if (totalAvailable < requested) {
            throw new IllegalArgumentException(
                    "Insufficient available stock. Requested: "
                    + requested + ", available: " + totalAvailable
            );
        }

        Reservation reservation = new Reservation();
        reservation.setReservationCode(
                "RES-" + UUID.randomUUID().toString()
                        .substring(0, 8).toUpperCase()
        );
        reservation.setMedicine(medicine);
        reservation.setRequestedQuantity(requested);
        reservation.setAllocatedQuantity(0);
        reservation.setCustomerReference(request.getCustomerReference());
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setRequestedAt(now);
        reservation.setExpiresAt(expiryTime);
        reservation.setNotes(request.getNotes());

        reservation = reservationRepository.saveAndFlush(reservation);

        int remaining = requested;

        for (InventoryBatch batch : batches) {

            if (remaining <= 0) {
                break;
            }

            int available = batch.getQuantityOnHand()
                    - batch.getQuantityReserved()
                    - batch.getQuantityQuarantined();

            int quantityToAllocate = Math.min(available, remaining);

            if (quantityToAllocate <= 0) {
                continue;
            }

            batch.setQuantityReserved(
                    batch.getQuantityReserved() + quantityToAllocate
            );

            batchRepository.save(batch);

            ReservationAllocation allocation =
                    new ReservationAllocation();

            allocation.setReservation(reservation);
            allocation.setBatch(batch);
            allocation.setAllocatedQuantity(quantityToAllocate);

            allocationRepository.save(allocation);

            remaining -= quantityToAllocate;
        }

        reservation.setAllocatedQuantity(requested);
        reservationRepository.save(reservation);

        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse confirmReservation(Long id) {

        Reservation reservation = findReservation(id);

        if (reservation.getStatus()
                != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING reservations can be confirmed."
            );
        }

        if (!reservation.getExpiresAt().isAfter(LocalDateTime.now())) {
            releaseAllocatedStock(reservation);
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
            return toResponse(reservation);
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {

        Reservation reservation = findReservation(id);

        if (reservation.getStatus()
                != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Only PENDING reservations can be cancelled."
            );
        }

        releaseAllocatedStock(reservation);

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return toResponse(reservation);
    }

    @Transactional
    public int expireReservations() {

        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndExpiresAtBefore(
                        Reservation.ReservationStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Reservation reservation : expiredReservations) {
            releaseAllocatedStock(reservation);
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);
        }

        return expiredReservations.size();
    }

    private void releaseAllocatedStock(Reservation reservation) {

        List<ReservationAllocation> allocations =
                allocationRepository.findByReservation_ReservationId(
                        reservation.getReservationId()
                );

        for (ReservationAllocation allocation : allocations) {

            InventoryBatch batch = allocation.getBatch();

            int reserved = batch.getQuantityReserved();
            int allocated = allocation.getAllocatedQuantity();

            if (reserved < allocated) {
                throw new IllegalStateException(
                        "Reserved stock is inconsistent for batch "
                        + batch.getBatchNumber()
                );
            }

            batch.setQuantityReserved(reserved - allocated);
            batchRepository.save(batch);
        }
    }

    private Reservation findReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reservation not found: " + id
                        ));
    }

    private ReservationResponse toResponse(Reservation reservation) {

        ReservationResponse response = new ReservationResponse();

        response.setReservationId(reservation.getReservationId());
        response.setReservationCode(reservation.getReservationCode());
        response.setMedicineId(
                reservation.getMedicine().getMedicineId()
        );
        response.setMedicineName(
                reservation.getMedicine().getGenericName()
        );
        response.setRequestedQuantity(reservation.getRequestedQuantity());
        response.setAllocatedQuantity(reservation.getAllocatedQuantity());
        response.setCustomerReference(reservation.getCustomerReference());
        response.setStatus(reservation.getStatus().name());
        response.setRequestedAt(reservation.getRequestedAt());
        response.setExpiresAt(reservation.getExpiresAt());
        response.setNotes(reservation.getNotes());

        List<ReservationAllocationResponse> allocationResponses =
                new ArrayList<>();

        List<ReservationAllocation> allocations =
                allocationRepository.findByReservation_ReservationId(
                        reservation.getReservationId()
                );

        for (ReservationAllocation allocation : allocations) {

            InventoryBatch batch = allocation.getBatch();

            ReservationAllocationResponse item =
                    new ReservationAllocationResponse();

            item.setAllocationId(allocation.getAllocationId());
            item.setBatchId(batch.getBatchId());
            item.setBatchNumber(batch.getBatchNumber());
            item.setExpiryDate(batch.getExpiryDate());
            item.setWarehouseId(
                    batch.getWarehouse().getWarehouseId()
            );
            item.setWarehouseName(
                    batch.getWarehouse().getWarehouseName()
            );
            item.setAllocatedQuantity(
                    allocation.getAllocatedQuantity()
            );

            allocationResponses.add(item);
        }

        response.setAllocations(allocationResponses);

        return response;
    }
}
