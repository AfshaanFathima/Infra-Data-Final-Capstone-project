
package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.ReservationAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationAllocationRepository
        extends JpaRepository<ReservationAllocation, Long> {

    List<ReservationAllocation> findByReservation_ReservationId(
            Long reservationId
    );
}
