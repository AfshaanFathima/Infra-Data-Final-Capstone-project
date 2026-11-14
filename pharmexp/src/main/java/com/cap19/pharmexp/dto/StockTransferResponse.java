
package com.cap19.pharmexp.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockTransferResponse {

    private Long transferId;
    private Long medicineId;
    private String medicineName;
    private Long sourceWarehouseId;
    private String sourceWarehouseName;
    private Long destinationWarehouseId;
    private String destinationWarehouseName;
    private Integer suggestedQuantity;
    private String transferReason;
    private String status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private LocalDate recommendationDate;

    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }

    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }

    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }

    public Long getSourceWarehouseId() { return sourceWarehouseId; }
    public void setSourceWarehouseId(Long sourceWarehouseId) { this.sourceWarehouseId = sourceWarehouseId; }

    public String getSourceWarehouseName() { return sourceWarehouseName; }
    public void setSourceWarehouseName(String sourceWarehouseName) { this.sourceWarehouseName = sourceWarehouseName; }

    public Long getDestinationWarehouseId() { return destinationWarehouseId; }
    public void setDestinationWarehouseId(Long destinationWarehouseId) { this.destinationWarehouseId = destinationWarehouseId; }

    public String getDestinationWarehouseName() { return destinationWarehouseName; }
    public void setDestinationWarehouseName(String destinationWarehouseName) { this.destinationWarehouseName = destinationWarehouseName; }

    public Integer getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }

    public String getTransferReason() { return transferReason; }
    public void setTransferReason(String transferReason) { this.transferReason = transferReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDate getRecommendationDate() { return recommendationDate; }
    public void setRecommendationDate(LocalDate recommendationDate) { this.recommendationDate = recommendationDate; }
}
