
package com.cap19.pharmexp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubstituteResponse {

    private Long substitutionId;
    private Long medicineId;
    private String medicineName;
    private Long substituteMedicineId;
    private String substituteMedicineName;
    private String approvalStatus;
    private String clinicalRationale;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDate effectiveFrom;
    private LocalDate effectiveUntil;

    public Long getSubstitutionId() { return substitutionId; }
    public void setSubstitutionId(Long substitutionId) { this.substitutionId = substitutionId; }

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public Long getSubstituteMedicineId() { return substituteMedicineId; }
    public void setSubstituteMedicineId(Long substituteMedicineId) { this.substituteMedicineId = substituteMedicineId; }

    public String getSubstituteMedicineName() { return substituteMedicineName; }
    public void setSubstituteMedicineName(String substituteMedicineName) { this.substituteMedicineName = substituteMedicineName; }

    public String getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }

    public String getClinicalRationale() { return clinicalRationale; }
    public void setClinicalRationale(String clinicalRationale) { this.clinicalRationale = clinicalRationale; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveUntil() { return effectiveUntil; }
    public void setEffectiveUntil(LocalDate effectiveUntil) { this.effectiveUntil = effectiveUntil; }
}
