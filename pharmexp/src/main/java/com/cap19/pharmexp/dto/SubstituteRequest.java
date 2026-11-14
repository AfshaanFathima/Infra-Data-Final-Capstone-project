
package com.cap19.pharmexp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class SubstituteRequest {

    @NotNull
    @Positive
    private Long medicineId;

    @NotNull
    @Positive
    private Long substituteMedicineId;

    @Size(max = 1000)
    private String clinicalRationale;

    private LocalDate effectiveFrom;
    private LocalDate effectiveUntil;

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

    public Long getSubstituteMedicineId() { return substituteMedicineId; }
    public void setSubstituteMedicineId(Long substituteMedicineId) { this.substituteMedicineId = substituteMedicineId; }

    public String getClinicalRationale() { return clinicalRationale; }
    public void setClinicalRationale(String clinicalRationale) { this.clinicalRationale = clinicalRationale; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveUntil() { return effectiveUntil; }
    public void setEffectiveUntil(LocalDate effectiveUntil) { this.effectiveUntil = effectiveUntil; }
}
