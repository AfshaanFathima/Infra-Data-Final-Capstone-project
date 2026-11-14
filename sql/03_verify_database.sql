
-- =========================================================
-- PharmaTrack - Database Verification Script
-- Purpose: Verify database tables, record counts and relationships
-- Run after:
-- 01_create_database.sql
-- 02_insert_sample_data.sql
-- =========================================================

USE pharma_inventory_db;


-- =========================================================
-- 1. DISPLAY ALL TABLES
-- =========================================================

SHOW TABLES;


-- =========================================================
-- 2. VERIFY RECORD COUNTS IN ALL 8 TABLES
-- Expected total: 141 records
-- =========================================================

SELECT 'medicines' AS table_name, COUNT(*) AS total_records
FROM medicines

UNION ALL

SELECT 'warehouses', COUNT(*)
FROM warehouses

UNION ALL

SELECT 'inventory_batches', COUNT(*)
FROM inventory_batches

UNION ALL

SELECT 'medicine_substitutes', COUNT(*)
FROM medicine_substitutes

UNION ALL

SELECT 'reservations', COUNT(*)
FROM reservations

UNION ALL

SELECT 'reservation_allocations', COUNT(*)
FROM reservation_allocations

UNION ALL

SELECT 'stock_transfers', COUNT(*)
FROM stock_transfers

UNION ALL

SELECT 'medicine_consumption', COUNT(*)
FROM medicine_consumption;


-- =========================================================
-- 3. VERIFY TOTAL NUMBER OF RECORDS
-- Expected result: 141
-- =========================================================

SELECT
    (SELECT COUNT(*) FROM medicines) +
    (SELECT COUNT(*) FROM warehouses) +
    (SELECT COUNT(*) FROM inventory_batches) +
    (SELECT COUNT(*) FROM medicine_substitutes) +
    (SELECT COUNT(*) FROM reservations) +
    (SELECT COUNT(*) FROM reservation_allocations) +
    (SELECT COUNT(*) FROM stock_transfers) +
    (SELECT COUNT(*) FROM medicine_consumption)
    AS total_database_records;


-- =========================================================
-- 4. VERIFY MEDICINE AND WAREHOUSE DATA
-- =========================================================

SELECT * FROM medicines
ORDER BY medicine_id;

SELECT * FROM warehouses
ORDER BY warehouse_id;


-- =========================================================
-- 5. VERIFY INVENTORY BATCHES
-- =========================================================

SELECT
    batch_id,
    medicine_id,
    warehouse_id,
    batch_number,
    quantity_on_hand,
    quantity_reserved,
    quantity_quarantined,
    unit_cost,
    expiry_date,
    batch_status
FROM inventory_batches
ORDER BY batch_id;


-- =========================================================
-- 6. VERIFY RESERVATIONS AND ALLOCATIONS
-- =========================================================

SELECT
    r.reservation_id,
    r.reservation_code,
    m.generic_name,
    r.requested_quantity,
    r.allocated_quantity,
    r.status
FROM reservations r
JOIN medicines m
    ON r.medicine_id = m.medicine_id
ORDER BY r.reservation_id;


SELECT
    allocation_id,
    reservation_id,
    batch_id,
    allocated_quantity
FROM reservation_allocations
ORDER BY allocation_id;


-- =========================================================
-- 7. VERIFY MEDICINE SUBSTITUTIONS
-- =========================================================

SELECT
    substitution_id,
    medicine_id,
    substitute_medicine_id,
    approval_status,
    clinical_rationale
FROM medicine_substitutes
ORDER BY substitution_id;


-- =========================================================
-- 8. VERIFY STOCK TRANSFERS
-- =========================================================

SELECT
    transfer_id,
    medicine_id,
    source_warehouse_id,
    destination_warehouse_id,
    suggested_quantity,
    transfer_reason,
    status
FROM stock_transfers
ORDER BY transfer_id;


-- =========================================================
-- 9. VERIFY MEDICINE CONSUMPTION
-- =========================================================

SELECT
    consumption_id,
    medicine_id,
    warehouse_id,
    consumption_date,
    consumed_quantity,
    reference_number
FROM medicine_consumption
ORDER BY consumption_id;


-- =========================================================
-- 10. CHECK FOR INVALID MEDICINE REFERENCES
-- Expected result: 0
-- =========================================================

SELECT COUNT(*) AS invalid_medicine_references
FROM inventory_batches b
LEFT JOIN medicines m
    ON b.medicine_id = m.medicine_id
WHERE m.medicine_id IS NULL;


-- =========================================================
-- 11. CHECK FOR INVALID WAREHOUSE REFERENCES
-- Expected result: 0
-- =========================================================

SELECT COUNT(*) AS invalid_warehouse_references
FROM inventory_batches b
LEFT JOIN warehouses w
    ON b.warehouse_id = w.warehouse_id
WHERE w.warehouse_id IS NULL;


-- =========================================================
-- 12. CHECK RESERVATION ALLOCATION REFERENCES
-- Expected result: 0
-- =========================================================

SELECT COUNT(*) AS invalid_allocation_references
FROM reservation_allocations a
LEFT JOIN reservations r
    ON a.reservation_id = r.reservation_id
LEFT JOIN inventory_batches b
    ON a.batch_id = b.batch_id
WHERE r.reservation_id IS NULL
   OR b.batch_id IS NULL;


-- =========================================================
-- END OF DATABASE VERIFICATION
-- =========================================================
