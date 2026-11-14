
package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.MedicineSubstitute;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MedicineSubstituteRepository
        extends JpaRepository<MedicineSubstitute, Long> {

    List<MedicineSubstitute> findByMedicine_MedicineId(Long medicineId);

    List<MedicineSubstitute> findByMedicine_MedicineIdAndApprovalStatus(
        Long medicineId,
        MedicineSubstitute.ApprovalStatus approvalStatus
    );

    boolean existsByMedicine_MedicineIdAndSubstituteMedicine_MedicineId(
        Long medicineId,
        Long substituteMedicineId
    );
}
