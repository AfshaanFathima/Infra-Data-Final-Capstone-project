package com.cap19.pharmexp.dto;

public class InventorySummaryResponse {

    private long totalBatches;
    private long totalUnitsOnHand;
    private long totalAvailableUnits;
    private long expiredBatches;
    private long nearExpiryBatches;
    private long quarantinedBatches;

    public long getTotalBatches() { return totalBatches; }
    public void setTotalBatches(long totalBatches) {
        this.totalBatches = totalBatches;
    }

    public long getTotalUnitsOnHand() { return totalUnitsOnHand; }
    public void setTotalUnitsOnHand(long totalUnitsOnHand) {
        this.totalUnitsOnHand = totalUnitsOnHand;
    }

    public long getTotalAvailableUnits() { return totalAvailableUnits; }
    public void setTotalAvailableUnits(long totalAvailableUnits) {
        this.totalAvailableUnits = totalAvailableUnits;
    }

    public long getExpiredBatches() { return expiredBatches; }
    public void setExpiredBatches(long expiredBatches) {
        this.expiredBatches = expiredBatches;
    }

    public long getNearExpiryBatches() { return nearExpiryBatches; }
    public void setNearExpiryBatches(long nearExpiryBatches) {
        this.nearExpiryBatches = nearExpiryBatches;
    }

    public long getQuarantinedBatches() { return quarantinedBatches; }
    public void setQuarantinedBatches(long quarantinedBatches) {
        this.quarantinedBatches = quarantinedBatches;
    }
}