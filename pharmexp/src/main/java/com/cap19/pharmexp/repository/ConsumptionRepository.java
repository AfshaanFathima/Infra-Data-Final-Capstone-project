package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.MedicineConsumption;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsumptionRepository extends JpaRepository<MedicineConsumption, Long> {
    List<MedicineConsumption> findByMedicine_MedicineId(Long medicineId);
}