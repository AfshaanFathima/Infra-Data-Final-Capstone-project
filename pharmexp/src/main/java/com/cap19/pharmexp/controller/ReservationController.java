
package com.cap19.pharmexp.controller;

import com.cap19.pharmexp.dto.ApiResponse;
import com.cap19.pharmexp.dto.ReservationRequest;
import com.cap19.pharmexp.dto.ReservationResponse;
import com.cap19.pharmexp.service.ReservationService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ApiResponse<List<ReservationResponse>> getAllReservations() {
        return new ApiResponse<>(
                true,
                "Reservations retrieved successfully.",
                reservationService.getAllReservations()
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ReservationResponse> getReservationById(
            @PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Reservation retrieved successfully.",
                reservationService.getReservationById(id)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        return new ApiResponse<>(
                true,
                "Reservation created and stock allocated using FEFO.",
                reservationService.createReservation(request)
        );
    }

    @PutMapping("/{id}/confirm")
    public ApiResponse<ReservationResponse> confirmReservation(
            @PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Reservation confirmation processed.",
                reservationService.confirmReservation(id)
        );
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<ReservationResponse> cancelReservation(
            @PathVariable Long id) {
        return new ApiResponse<>(
                true,
                "Reservation cancelled and allocated stock released.",
                reservationService.cancelReservation(id)
        );
    }

    @PostMapping("/expire")
    public ApiResponse<Map<String, Integer>> expireReservations() {
        int count = reservationService.expireReservations();

        return new ApiResponse<>(
                true,
                "Expired reservations processed and stock released.",
                Map.of("expiredReservations", count)
        );
    }
}
