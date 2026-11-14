
package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.SubstituteRequest;
import com.cap19.pharmexp.dto.SubstituteResponse;
import com.cap19.pharmexp.entity.Medicine;
import com.cap19.pharmexp.entity.MedicineSubstitute;
import com.cap19.pharmexp.repository.MedicineRepository;
import com.cap19.pharmexp.repository.MedicineSubstituteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SubstituteService {

    private final MedicineSubstituteRepository substituteRepository;
    private final MedicineRepository medicineRepository;

    public SubstituteService(
            MedicineSubstituteRepository substituteRepository,
            MedicineRepository medicineRepository) {
        this.substituteRepository = substituteRepository;
        this.medicineRepository = medicineRepository;
    }

    public List<SubstituteResponse> getAllSubstitutes() {
        return substituteRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SubstituteResponse> getSubstitutesForMedicine(Long medicineId) {
        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Medicine not found: " + medicineId);
        }

        return substituteRepository.findByMedicine_MedicineId(medicineId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<SubstituteResponse> getApprovedSubstitutes(Long medicineId) {
        if (!medicineRepository.existsById(medicineId)) {
            throw new RuntimeException("Medicine not found: " + medicineId);
        }

        LocalDate today = LocalDate.now();

        return substituteRepository.findByMedicine_MedicineIdAndApprovalStatus(
                        medicineId,
                        MedicineSubstitute.ApprovalStatus.APPROVED
                )
                .stream()
                .filter(item ->
                        item.getEffectiveFrom() == null ||
                        !item.getEffectiveFrom().isAfter(today))
                .filter(item ->
                        item.getEffectiveUntil() == null ||
                        !item.getEffectiveUntil().isBefore(today))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SubstituteResponse createSubstitute(SubstituteRequest request) {

        if (request.getMedicineId().equals(request.getSubstituteMedicineId())) {
            throw new IllegalArgumentException(
                    "A medicine cannot be its own substitute."
            );
        }

        if (request.getEffectiveFrom() != null &&
                request.getEffectiveUntil() != null &&
                request.getEffectiveUntil().isBefore(request.getEffectiveFrom())) {
            throw new IllegalArgumentException(
                    "Effective-until date cannot be before effective-from date."
            );
        }

        Medicine medicine = medicineRepository.findById(request.getMedicineId())
                .orElseThrow(() -> new RuntimeException("Original medicine not found."));

        Medicine substituteMedicine = medicineRepository
                .findById(request.getSubstituteMedicineId())
                .orElseThrow(() -> new RuntimeException("Substitute medicine not found."));

        if (!Boolean.TRUE.equals(medicine.getIsActive()) ||
    !Boolean.TRUE.equals(substituteMedicine.getIsActive())) {

    throw new IllegalArgumentException(
            "Both medicines must be active."
    );
}

        boolean alreadyExists =
                substituteRepository
                .existsByMedicine_MedicineIdAndSubstituteMedicine_MedicineId(
                        request.getMedicineId(),
                        request.getSubstituteMedicineId()
                );

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "This substitute relationship already exists."
            );
        }

        MedicineSubstitute substitute = new MedicineSubstitute();
        substitute.setMedicine(medicine);
        substitute.setSubstituteMedicine(substituteMedicine);
        substitute.setClinicalRationale(request.getClinicalRationale());
        substitute.setEffectiveFrom(request.getEffectiveFrom());
        substitute.setEffectiveUntil(request.getEffectiveUntil());

        // New substitutions always require approval.
        substitute.setApprovalStatus(
                MedicineSubstitute.ApprovalStatus.PENDING
        );

        return toResponse(substituteRepository.save(substitute));
    }

    @Transactional
    public SubstituteResponse updateApproval(
            Long substitutionId,
            String status,
            String approvedBy) {

        MedicineSubstitute substitute = substituteRepository.findById(substitutionId)
                .orElseThrow(() ->
                        new RuntimeException("Substitution not found: " + substitutionId));

        MedicineSubstitute.ApprovalStatus newStatus;

        try {
            newStatus = MedicineSubstitute.ApprovalStatus.valueOf(
                    status.trim().toUpperCase()
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Status must be APPROVED or REJECTED."
            );
        }

        if (newStatus == MedicineSubstitute.ApprovalStatus.PENDING) {
            throw new IllegalArgumentException(
                    "Approval status cannot be changed back to PENDING."
            );
        }

        substitute.setApprovalStatus(newStatus);

        if (newStatus == MedicineSubstitute.ApprovalStatus.APPROVED) {
            if (approvedBy == null || approvedBy.isBlank()) {
                throw new IllegalArgumentException(
                        "approvedBy is required when approving a substitute."
                );
            }

            substitute.setApprovedBy(approvedBy.trim());
            substitute.setApprovedAt(LocalDateTime.now());
        } else {
            substitute.setApprovedBy(null);
            substitute.setApprovedAt(null);
        }

        return toResponse(substituteRepository.save(substitute));
    }

    private SubstituteResponse toResponse(MedicineSubstitute item) {

        SubstituteResponse response = new SubstituteResponse();

        response.setSubstitutionId(item.getSubstitutionId());
        response.setMedicineId(item.getMedicine().getMedicineId());
        response.setMedicineName(item.getMedicine().getGenericName());

        response.setSubstituteMedicineId(
                item.getSubstituteMedicine().getMedicineId()
        );
        response.setSubstituteMedicineName(
                item.getSubstituteMedicine().getGenericName()
        );

        response.setApprovalStatus(item.getApprovalStatus().name());
        response.setClinicalRationale(item.getClinicalRationale());
        response.setApprovedBy(item.getApprovedBy());
        response.setApprovedAt(item.getApprovedAt());
        response.setEffectiveFrom(item.getEffectiveFrom());
        response.setEffectiveUntil(item.getEffectiveUntil());

        return response;
    }
}
