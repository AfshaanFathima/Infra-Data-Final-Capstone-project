package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    Optional<Medicine> findByMedicineCode(String medicineCode);
    boolean existsByMedicineCode(String medicineCode);
}