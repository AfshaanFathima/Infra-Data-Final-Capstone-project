package com.cap19.pharmexp.service;

import com.cap19.pharmexp.dto.MedicineRequest;
import com.cap19.pharmexp.dto.MedicineResponse;
import com.cap19.pharmexp.entity.Medicine;
import com.cap19.pharmexp.repository.MedicineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<MedicineResponse> getAllMedicines() {
        return medicineRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public MedicineResponse getMedicineById(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found: " + id));

        return convertToResponse(medicine);
    }

    public MedicineResponse createMedicine(MedicineRequest request) {
        if (medicineRepository.existsByMedicineCode(request.getMedicineCode())) {
            throw new IllegalArgumentException("Medicine code already exists");
        }

        Medicine medicine = new Medicine();
        copyRequestToEntity(request, medicine);
        medicine.setIsActive(true);

        return convertToResponse(medicineRepository.save(medicine));
    }

    public MedicineResponse updateMedicine(Long id, MedicineRequest request) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found: " + id));

        medicineRepository.findByMedicineCode(request.getMedicineCode())
                .filter(existing -> !existing.getMedicineId().equals(id))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Medicine code already exists");
                });

        copyRequestToEntity(request, medicine);

        return convertToResponse(medicineRepository.save(medicine));
    }

    public void deleteMedicine(Long id) {
        Medicine medicine = medicineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicine not found: " + id));

        medicineRepository.delete(medicine);
    }

    private void copyRequestToEntity(MedicineRequest request, Medicine medicine) {
        medicine.setMedicineCode(request.getMedicineCode());
        medicine.setGenericName(request.getGenericName());
        medicine.setBrandName(request.getBrandName());
        medicine.setStrength(request.getStrength());
        medicine.setDosageForm(request.getDosageForm());
        medicine.setManufacturer(request.getManufacturer());
        medicine.setUnitOfMeasure(request.getUnitOfMeasure());
    }

    private MedicineResponse convertToResponse(Medicine medicine) {
        MedicineResponse response = new MedicineResponse();

        response.setMedicineId(medicine.getMedicineId());
        response.setMedicineCode(medicine.getMedicineCode());
        response.setGenericName(medicine.getGenericName());
        response.setBrandName(medicine.getBrandName());
        response.setStrength(medicine.getStrength());
        response.setDosageForm(medicine.getDosageForm());
        response.setManufacturer(medicine.getManufacturer());
        response.setUnitOfMeasure(medicine.getUnitOfMeasure());
        response.setIsActive(medicine.getIsActive());

        return response;
    }
}