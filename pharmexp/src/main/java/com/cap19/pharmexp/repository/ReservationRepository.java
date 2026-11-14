
package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationCode(String reservationCode);

    boolean existsByReservationCode(String reservationCode);

    List<Reservation> findByStatusAndExpiresAtBefore(
            Reservation.ReservationStatus status,
            LocalDateTime time
    );
}
