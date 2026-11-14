
-- =========================================================
-- PharmaTrack - Sample Database Records
-- Recreates the records from the original database CSV exports
-- Total records: 141 across 8 tables
-- Prerequisite: Run 01_create_database.sql first
-- IMPORTANT: Run only on a newly created, empty database.
-- =========================================================

USE pharma_inventory_db;

START TRANSACTION;

-- =========================================================
-- 1. MEDICINES (19 RECORDS)
-- =========================================================

INSERT INTO medicines
(medicine_id, medicine_code, generic_name, brand_name, strength, dosage_form, manufacturer, unit_of_measure, is_active, created_at, updated_at)
VALUES
(1,'MED-001','Amoxicillin','AlphaCare','500 mg','Capsule','ABC Pharmaceuticals','Capsule',1,'2026-11-10 22:10:31','2026-11-10 22:10:31'),
(2,'MED-002','Amoxicillin','BetaCare','500 mg','Capsule','XYZ Pharmaceuticals','Capsule',1,'2026-11-10 22:10:32','2026-11-10 22:10:32'),
(3,'MED-003','Cetirizine','CetiCare','10 mg','Tablet','Health Pharma','Tablet',1,'2026-11-10 22:10:32','2026-11-10 22:10:32'),
(4,'BULK-MED-001','Sample Medicine 1','Brand 1','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(5,'BULK-MED-002','Sample Medicine 2','Brand 2','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(6,'BULK-MED-003','Sample Medicine 3','Brand 3','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(7,'BULK-MED-004','Sample Medicine 4','Brand 4','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,'BULK-MED-005','Sample Medicine 5','Brand 5','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(9,'BULK-MED-006','Sample Medicine 6','Brand 6','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(10,'BULK-MED-007','Sample Medicine 7','Brand 7','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(11,'BULK-MED-008','Sample Medicine 8','Brand 8','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(12,'BULK-MED-009','Sample Medicine 9','Brand 9','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(13,'BULK-MED-010','Sample Medicine 10','Brand 10','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(14,'BULK-MED-011','Sample Medicine 11','Brand 11','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(15,'BULK-MED-012','Sample Medicine 12','Brand 12','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(16,'BULK-MED-013','Sample Medicine 13','Brand 13','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(17,'BULK-MED-014','Sample Medicine 14','Brand 14','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(18,'BULK-MED-015','Sample Medicine 15','Brand 15','500 mg','Tablet','Sample Pharmaceuticals','Tablet',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(19,'TEST-MED-20261111-01','Test Medicine','PharmaTest','500 mg','Tablet','Test Pharma','Tablet',1,'2026-11-11 07:29:04','2026-11-11 07:29:04');


-- =========================================================
-- 2. WAREHOUSES (17 RECORDS)
-- =========================================================

INSERT INTO warehouses
(warehouse_id, warehouse_code, warehouse_name, address, city, state, postal_code, contact_person, contact_phone, is_active, created_at, updated_at)
VALUES
(1,'WH-001','Chennai Central Distribution Warehouse','Anna Salai, Chennai','Chennai','Tamil Nadu','600002','Arun Kumar','9000000001',1,'2026-11-10 22:10:32','2026-11-12 05:08:18'),
(2,'WH-002','Bengaluru Regional Distribution Warehouse','Whitefield Main Road, Bengaluru','Bengaluru','Karnataka','560066','Priya Sharma','9000000002',1,'2026-11-10 22:10:32','2026-11-12 05:08:18'),
(3,'BULK-WH-001','Coimbatore Regional Warehouse','Gandhi Road, Coimbatore','Coimbatore','Tamil Nadu','641001','Karthik Raj','9000000003',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(4,'BULK-WH-002','Madurai Distribution Centre','Gandhi Market Road, Madurai','Madurai','Tamil Nadu','625001','Meena Devi','9000000004',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(5,'BULK-WH-003','Tiruchirappalli Regional Warehouse','Salem Main Road, Tiruchirappalli','Tiruchirappalli','Tamil Nadu','620002','Suresh Babu','9000000005',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(6,'BULK-WH-004','Salem Distribution Centre','Town Hall Road, Salem','Salem','Tamil Nadu','636001','Divya Kumar','9000000006',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(7,'BULK-WH-005','Thoothukudi Regional Warehouse','Beach Road, Thoothukudi','Thoothukudi','Tamil Nadu','628001','Ramesh Anand','9000000007',1,'2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,'BULK-WH-006','Tirunelveli Distribution Centre','Nehru Street, Tirunelveli','Tirunelveli','Tamil Nadu','627001','Lakshmi Priya','9000000008',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(9,'BULK-WH-007','Erode Regional Warehouse','Market Road, Erode','Erode','Tamil Nadu','638001','Vijay Kumar','9000000009',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(10,'BULK-WH-008','Vellore Distribution Centre','Railway Station Road, Vellore','Vellore','Tamil Nadu','632001','Anitha Raj','9000000010',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(11,'BULK-WH-009','Thanjavur Regional Warehouse','Bus Stand Road, Thanjavur','Thanjavur','Tamil Nadu','613001','Prakash M','9000000011',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(12,'BULK-WH-010','Dindigul Distribution Centre','Main Road, Dindigul','Dindigul','Tamil Nadu','624001','Nandhini S','9000000012',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(13,'BULK-WH-011','Karur Regional Warehouse','Hospital Road, Karur','Karur','Tamil Nadu','639001','Senthil Kumar','9000000013',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(14,'BULK-WH-012','Tiruppur Distribution Centre','Market Street, Tiruppur','Tiruppur','Tamil Nadu','641601','Revathi R','9000000014',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(15,'BULK-WH-013','Kanchipuram Regional Warehouse','Temple Road, Kanchipuram','Kanchipuram','Tamil Nadu','631501','Gokul Krishnan','9000000015',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(16,'BULK-WH-014','Cuddalore Distribution Centre','Collector Office Road, Cuddalore','Cuddalore','Tamil Nadu','607001','Saranya Devi','9000000016',1,'2026-11-10 22:17:09','2026-11-12 05:08:18'),
(17,'BULK-WH-015','Namakkal Regional Warehouse','Main Bazaar Road, Namakkal','Namakkal','Tamil Nadu','637001','Mohan Raj','9000000017',1,'2026-11-10 22:17:09','2026-11-12 05:08:18');


-- =========================================================
-- 3. INVENTORY BATCHES (19 RECORDS)
-- =========================================================

INSERT INTO inventory_batches
(batch_id, medicine_id, warehouse_id, batch_number, manufacturing_date, expiry_date, received_quantity, quantity_on_hand, quantity_reserved, quantity_quarantined, unit_cost, batch_status, created_at, updated_at)
VALUES
(1,1,1,'AMX-CH-001','2026-01-01','2027-01-31',120,100,22,0,12.50,'ACTIVE','2026-11-10 22:10:32','2026-11-11 04:46:40'),
(2,1,1,'AMX-CH-002','2026-03-01','2027-06-30',100,80,10,0,13.00,'ACTIVE','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(3,2,2,'AMX-BL-001','2026-02-01','2027-08-31',100,100,0,0,11.50,'ACTIVE','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(4,3,1,'CET-CH-001','2026-04-01','2028-04-30',60,60,0,0,5.00,'ACTIVE','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(5,4,3,'BULK-BATCH-001','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(6,5,4,'BULK-BATCH-002','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(7,6,5,'BULK-BATCH-003','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,7,6,'BULK-BATCH-004','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(9,8,7,'BULK-BATCH-005','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(10,9,8,'BULK-BATCH-006','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(11,10,9,'BULK-BATCH-007','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(12,11,10,'BULK-BATCH-008','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(13,12,11,'BULK-BATCH-009','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(14,13,12,'BULK-BATCH-010','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(15,14,13,'BULK-BATCH-011','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(16,15,14,'BULK-BATCH-012','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(17,16,15,'BULK-BATCH-013','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(18,17,16,'BULK-BATCH-014','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(19,18,17,'BULK-BATCH-015','2026-01-01','2027-12-31',100,100,10,0,15.00,'ACTIVE','2026-11-10 22:17:09','2026-11-10 22:17:09');


-- =========================================================
-- 4. MEDICINE SUBSTITUTES (16 RECORDS)
-- =========================================================

INSERT INTO medicine_substitutes
(substitution_id, medicine_id, substitute_medicine_id, approval_status, clinical_rationale, approved_by, approved_at, effective_from, effective_until, created_at, updated_at)
VALUES
(1,1,2,'APPROVED','Fictional project data: approved alternative entry for testing.','Project Admin','2026-11-10 22:10:32','2026-01-01','2027-12-31','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(2,4,5,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(3,5,6,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(4,6,7,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(5,7,8,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(6,8,9,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(7,9,10,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,10,11,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(9,11,12,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(10,12,13,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(11,13,14,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(12,14,15,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(13,15,16,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(14,16,17,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(15,17,18,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(16,18,4,'APPROVED','Fictional test data for substitution workflow.','Project Admin','2026-11-10 22:17:09','2026-01-01','2027-12-31','2026-11-10 22:17:09','2026-11-10 22:17:09');


-- =========================================================
-- 5. RESERVATIONS (18 RECORDS)
-- =========================================================

INSERT INTO reservations
(reservation_id, reservation_code, medicine_id, requested_quantity, allocated_quantity, customer_reference, status, requested_at, expires_at, notes, created_at, updated_at)
VALUES
(1,'RES-001',1,30,30,'CUST-001','PENDING','2026-11-10 22:10:32','2026-12-01 23:59:59','Sample reservation using two inventory batches','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(2,'BULK-RES-001',4,10,10,'BULK-CUST-001','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(3,'BULK-RES-002',5,10,10,'BULK-CUST-002','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(4,'BULK-RES-003',6,10,10,'BULK-CUST-003','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(5,'BULK-RES-004',7,10,10,'BULK-CUST-004','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(6,'BULK-RES-005',8,10,10,'BULK-CUST-005','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(7,'BULK-RES-006',9,10,10,'BULK-CUST-006','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,'BULK-RES-007',10,10,10,'BULK-CUST-007','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(9,'BULK-RES-008',11,10,10,'BULK-CUST-008','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(10,'BULK-RES-009',12,10,10,'BULK-CUST-009','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(11,'BULK-RES-010',13,10,10,'BULK-CUST-010','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(12,'BULK-RES-011',14,10,10,'BULK-CUST-011','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(13,'BULK-RES-012',15,10,10,'BULK-CUST-012','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(14,'BULK-RES-013',16,10,10,'BULK-CUST-013','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(15,'BULK-RES-014',17,10,10,'BULK-CUST-014','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(16,'BULK-RES-015',18,10,10,'BULK-CUST-015','PENDING','2026-11-10 22:17:09','2026-12-31 23:59:59','Sample bulk reservation','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(17,'RES-D2C7CE64',1,2,2,'SWAGGER-TEST-001','CONFIRMED','2026-11-11 04:38:34','2026-11-12 23:59:00','Testing reservation through Swagger','2026-11-11 04:38:34','2026-11-11 04:43:30'),
(18,'RES-87772893',1,1,1,'SWAGGER-CANCEL-001','CANCELLED','2026-11-11 04:46:13','2026-11-12 23:59:00','Testing reservation cancellation','2026-11-11 04:46:12','2026-11-11 04:46:40');


-- =========================================================
-- 6. RESERVATION ALLOCATIONS (19 RECORDS)
-- =========================================================

INSERT INTO reservation_allocations
(allocation_id, reservation_id, batch_id, allocated_quantity, allocated_at)
VALUES
(1,1,1,20,'2026-11-10 22:10:32'),
(2,1,2,10,'2026-11-10 22:10:32'),
(3,2,5,10,'2026-11-10 22:17:09'),
(4,3,6,10,'2026-11-10 22:17:09'),
(5,4,7,10,'2026-11-10 22:17:09'),
(6,5,8,10,'2026-11-10 22:17:09'),
(7,6,9,10,'2026-11-10 22:17:09'),
(8,7,10,10,'2026-11-10 22:17:09'),
(9,8,11,10,'2026-11-10 22:17:09'),
(10,9,12,10,'2026-11-10 22:17:09'),
(11,10,13,10,'2026-11-10 22:17:09'),
(12,11,14,10,'2026-11-10 22:17:09'),
(13,12,15,10,'2026-11-10 22:17:09'),
(14,13,16,10,'2026-11-10 22:17:09'),
(15,14,17,10,'2026-11-10 22:17:09'),
(16,15,18,10,'2026-11-10 22:17:09'),
(17,16,19,10,'2026-11-10 22:17:09'),
(18,17,1,2,'2026-11-11 04:38:35'),
(19,18,1,1,'2026-11-11 04:46:13');


-- =========================================================
-- 7. STOCK TRANSFERS (16 RECORDS)
-- Original CSV descriptions retained without modification.
-- =========================================================

INSERT INTO stock_transfers
(transfer_id, medicine_id, source_warehouse_id, destination_warehouse_id, suggested_quantity, transfer_reason, status, reviewed_by, reviewed_at, recommendation_date, created_at, updated_at)
VALUES
(1,1,1,2,15,'Sample recommendation for warehouse stock balancing','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:10:32','2026-11-10 22:10:32'),
(2,4,3,4,20,'Sample stock transfer recommendation 1','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(3,5,4,5,20,'Sample stock transfer recommendation 2','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(4,6,5,6,20,'Sample stock transfer recommendation 3','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(5,7,6,7,20,'Sample stock transfer recommendation 4','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(6,8,7,8,20,'Sample stock transfer recommendation 5','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(7,9,8,9,20,'Sample stock transfer recommendation 6','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(8,10,9,10,20,'Sample stock transfer recommendation 7','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(9,11,10,11,20,'Sample stock transfer recommendation 8','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(10,12,11,12,20,'Sample stock transfer recommendation 9','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(11,13,12,13,20,'Sample stock transfer recommendation 10','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(12,14,13,14,20,'Sample stock transfer recommendation 11','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(13,15,14,15,20,'Sample stock transfer recommendation 12','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(14,16,15,16,20,'Sample stock transfer recommendation 13','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(15,17,16,17,20,'Sample stock transfer recommendation 14','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09'),
(16,18,17,3,20,'Sample stock transfer recommendation 15','RECOMMENDED',NULL,NULL,'2026-11-10','2026-11-10 22:17:09','2026-11-10 22:17:09');


-- =========================================================
-- 8. MEDICINE CONSUMPTION (17 RECORDS)
-- =========================================================

INSERT INTO medicine_consumption
(consumption_id, medicine_id, warehouse_id, consumption_date, consumed_quantity, reference_number, created_at)
VALUES
(1,1,1,'2026-11-01',15,'CON-001','2026-11-10 22:10:32'),
(2,3,1,'2026-11-02',8,'CON-002','2026-11-10 22:10:32'),
(3,4,3,'2026-11-02',5,'BULK-CON-001','2026-11-10 22:17:09'),
(4,5,4,'2026-11-03',10,'BULK-CON-002','2026-11-10 22:17:09'),
(5,6,5,'2026-11-04',15,'BULK-CON-003','2026-11-10 22:17:09'),
(6,7,6,'2026-11-05',20,'BULK-CON-004','2026-11-10 22:17:09'),
(7,8,7,'2026-11-06',25,'BULK-CON-005','2026-11-10 22:17:09'),
(8,9,8,'2026-11-07',30,'BULK-CON-006','2026-11-10 22:17:09'),
(9,10,9,'2026-11-08',35,'BULK-CON-007','2026-11-10 22:17:09'),
(10,11,10,'2026-11-09',40,'BULK-CON-008','2026-11-10 22:17:09'),
(11,12,11,'2026-11-10',45,'BULK-CON-009','2026-11-10 22:17:09'),
(12,13,12,'2026-11-11',50,'BULK-CON-010','2026-11-10 22:17:09'),
(13,14,13,'2026-11-12',55,'BULK-CON-011','2026-11-10 22:17:09'),
(14,15,14,'2026-11-13',60,'BULK-CON-012','2026-11-10 22:17:09'),
(15,16,15,'2026-11-14',65,'BULK-CON-013','2026-11-10 22:17:09'),
(16,17,16,'2026-11-15',70,'BULK-CON-014','2026-11-10 22:17:09'),
(17,18,17,'2026-11-16',75,'BULK-CON-015','2026-11-10 22:17:09');

COMMIT;

-- =========================================================
-- END OF SAMPLE DATA INSERT SCRIPT
-- =========================================================
