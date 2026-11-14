
package com.cap19.pharmexp.repository;

import com.cap19.pharmexp.entity.StockTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockTransferRepository
        extends JpaRepository<StockTransfer, Long> {

    List<StockTransfer> findByStatus(
        StockTransfer.TransferStatus status
    );

    List<StockTransfer> findByMedicine_MedicineId(Long medicineId);
}
