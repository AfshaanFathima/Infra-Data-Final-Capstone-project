
package com.cap19.pharmexp.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers")
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id", nullable = false)
    private Medicine medicine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_warehouse_id", nullable = false)
    private Warehouse sourceWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_warehouse_id", nullable = false)
    private Warehouse destinationWarehouse;

    @Column(name = "suggested_quantity", nullable = false)
    private Integer suggestedQuantity;

    @Column(name = "transfer_reason")
    private String transferReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status = TransferStatus.RECOMMENDED;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "recommendation_date")
    private LocalDate recommendationDate;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public enum TransferStatus {
        RECOMMENDED, APPROVED, REJECTED, CANCELLED
    }

    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }

    public Medicine getMedicine() { return medicine; }
    public void setMedicine(Medicine medicine) { this.medicine = medicine; }

    public Warehouse getSourceWarehouse() { return sourceWarehouse; }
    public void setSourceWarehouse(Warehouse sourceWarehouse) { this.sourceWarehouse = sourceWarehouse; }

    public Warehouse getDestinationWarehouse() { return destinationWarehouse; }
    public void setDestinationWarehouse(Warehouse destinationWarehouse) { this.destinationWarehouse = destinationWarehouse; }

    public Integer getSuggestedQuantity() { return suggestedQuantity; }
    public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }

    public String getTransferReason() { return transferReason; }
    public void setTransferReason(String transferReason) { this.transferReason = transferReason; }

    public TransferStatus getStatus() { return status; }
    public void setStatus(TransferStatus status) { this.status = status; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    public LocalDate getRecommendationDate() { return recommendationDate; }
    public void setRecommendationDate(LocalDate recommendationDate) { this.recommendationDate = recommendationDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
