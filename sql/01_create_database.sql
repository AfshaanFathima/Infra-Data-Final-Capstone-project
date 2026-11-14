
-- ============================================================
-- CAP-19 PHARMATRACK
-- FILE: 01_create_database.sql
-- PURPOSE: Create the database and all 8 project tables
-- ============================================================

-- 1. CREATE DATABASE

CREATE DATABASE IF NOT EXISTS pharma_inventory_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_0900_ai_ci;

USE pharma_inventory_db;


-- ============================================================
-- 2. MEDICINES
-- ============================================================

CREATE TABLE IF NOT EXISTS medicines (
    medicine_id BIGINT NOT NULL AUTO_INCREMENT,
    medicine_code VARCHAR(30) NOT NULL,
    generic_name VARCHAR(150) NOT NULL,
    brand_name VARCHAR(150) DEFAULT NULL,
    strength VARCHAR(50) DEFAULT NULL,
    dosage_form VARCHAR(50) DEFAULT NULL,
    manufacturer VARCHAR(150) DEFAULT NULL,
    unit_of_measure VARCHAR(30) DEFAULT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (medicine_id),
    UNIQUE KEY medicine_code (medicine_code)
) ENGINE=InnoDB;


-- ============================================================
-- 3. WAREHOUSES
-- ============================================================

CREATE TABLE IF NOT EXISTS warehouses (
    warehouse_id BIGINT NOT NULL AUTO_INCREMENT,
    warehouse_code VARCHAR(30) NOT NULL,
    warehouse_name VARCHAR(150) NOT NULL,
    address VARCHAR(255) DEFAULT NULL,
    city VARCHAR(100) DEFAULT NULL,
    state VARCHAR(100) DEFAULT NULL,
    postal_code VARCHAR(20) DEFAULT NULL,
    contact_person VARCHAR(100) DEFAULT NULL,
    contact_phone VARCHAR(20) DEFAULT NULL,
    is_active TINYINT(1) DEFAULT 1,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (warehouse_id),
    UNIQUE KEY warehouse_code (warehouse_code)
) ENGINE=InnoDB;


-- ============================================================
-- 4. INVENTORY BATCHES
-- ============================================================

CREATE TABLE IF NOT EXISTS inventory_batches (
    batch_id BIGINT NOT NULL AUTO_INCREMENT,
    medicine_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    batch_number VARCHAR(50) NOT NULL,
    manufacturing_date DATE DEFAULT NULL,
    expiry_date DATE NOT NULL,
    received_quantity INT NOT NULL,
    quantity_on_hand INT NOT NULL,
    quantity_reserved INT DEFAULT 0,
    quantity_quarantined INT DEFAULT 0,
    unit_cost DECIMAL(12,2) NOT NULL,
    batch_status ENUM(
        'ACTIVE',
        'QUARANTINED',
        'RECALLED',
        'DEPLETED'
    ) DEFAULT 'ACTIVE',
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (batch_id),

    UNIQUE KEY uq_batch_location (
        medicine_id,
        warehouse_id,
        batch_number
    ),

    KEY idx_batch_expiry (expiry_date),
    KEY idx_batch_medicine (medicine_id),
    KEY idx_batch_warehouse (warehouse_id),

    CONSTRAINT fk_batch_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT fk_batch_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses (warehouse_id),

    CONSTRAINT chk_batch_cost
        CHECK (unit_cost >= 0),

    CONSTRAINT chk_batch_quantities
        CHECK (
            received_quantity >= 0
            AND quantity_on_hand >= 0
            AND quantity_reserved >= 0
            AND quantity_quarantined >= 0
            AND quantity_reserved + quantity_quarantined
                <= quantity_on_hand
        )
) ENGINE=InnoDB;


-- ============================================================
-- 5. MEDICINE SUBSTITUTES
-- ============================================================

CREATE TABLE IF NOT EXISTS medicine_substitutes (
    substitution_id BIGINT NOT NULL AUTO_INCREMENT,
    medicine_id BIGINT NOT NULL,
    substitute_medicine_id BIGINT NOT NULL,
    approval_status ENUM(
        'PENDING',
        'APPROVED',
        'REJECTED'
    ) DEFAULT 'PENDING',
    clinical_rationale TEXT,
    approved_by VARCHAR(100) DEFAULT NULL,
    approved_at DATETIME DEFAULT NULL,
    effective_from DATE DEFAULT NULL,
    effective_until DATE DEFAULT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (substitution_id),

    UNIQUE KEY uq_medicine_substitution (
        medicine_id,
        substitute_medicine_id
    ),

    KEY idx_substitution_medicine (medicine_id),
    KEY idx_substitution_alternative (substitute_medicine_id),
    KEY idx_substitution_status (approval_status),

    CONSTRAINT fk_alternative_medicine
        FOREIGN KEY (substitute_medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT fk_substitute_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT chk_no_self_substitution
        CHECK (medicine_id <> substitute_medicine_id),

    CONSTRAINT chk_substitution_dates
        CHECK (
            effective_until IS NULL
            OR effective_from IS NULL
            OR effective_until >= effective_from
        )
) ENGINE=InnoDB;


-- ============================================================
-- 6. RESERVATIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS reservations (
    reservation_id BIGINT NOT NULL AUTO_INCREMENT,
    reservation_code VARCHAR(50) NOT NULL,
    medicine_id BIGINT NOT NULL,
    requested_quantity INT NOT NULL,
    allocated_quantity INT DEFAULT 0,
    customer_reference VARCHAR(100) DEFAULT NULL,
    status ENUM(
        'PENDING',
        'CONFIRMED',
        'CANCELLED',
        'EXPIRED'
    ) DEFAULT 'PENDING',
    requested_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at DATETIME DEFAULT NULL,
    notes TEXT,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (reservation_id),
    UNIQUE KEY reservation_code (reservation_code),

    KEY idx_reservation_medicine (medicine_id),
    KEY idx_reservation_status (status),
    KEY idx_reservation_expiry (expires_at),

    CONSTRAINT fk_reservation_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT chk_allocated_quantity
        CHECK (
            allocated_quantity >= 0
            AND allocated_quantity <= requested_quantity
        ),

    CONSTRAINT chk_requested_quantity
        CHECK (requested_quantity > 0)
) ENGINE=InnoDB;


-- ============================================================
-- 7. RESERVATION ALLOCATIONS
-- ============================================================

CREATE TABLE IF NOT EXISTS reservation_allocations (
    allocation_id BIGINT NOT NULL AUTO_INCREMENT,
    reservation_id BIGINT NOT NULL,
    batch_id BIGINT NOT NULL,
    allocated_quantity INT NOT NULL,
    allocated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (allocation_id),

    UNIQUE KEY uq_reservation_batch (
        reservation_id,
        batch_id
    ),

    KEY idx_allocation_reservation (reservation_id),
    KEY idx_allocation_batch (batch_id),

    CONSTRAINT fk_allocation_batch
        FOREIGN KEY (batch_id)
        REFERENCES inventory_batches (batch_id),

    CONSTRAINT fk_allocation_reservation
        FOREIGN KEY (reservation_id)
        REFERENCES reservations (reservation_id),

    CONSTRAINT chk_allocation_quantity
        CHECK (allocated_quantity > 0)
) ENGINE=InnoDB;


-- ============================================================
-- 8. STOCK TRANSFERS
-- ============================================================

CREATE TABLE IF NOT EXISTS stock_transfers (
    transfer_id BIGINT NOT NULL AUTO_INCREMENT,
    medicine_id BIGINT NOT NULL,
    source_warehouse_id BIGINT NOT NULL,
    destination_warehouse_id BIGINT NOT NULL,
    suggested_quantity INT NOT NULL,
    transfer_reason TEXT,
    status ENUM(
        'RECOMMENDED',
        'APPROVED',
        'REJECTED',
        'CANCELLED'
    ) DEFAULT 'RECOMMENDED',
    reviewed_by VARCHAR(100) DEFAULT NULL,
    reviewed_at DATETIME DEFAULT NULL,
    recommendation_date DATE DEFAULT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (transfer_id),

    KEY idx_transfer_medicine (medicine_id),
    KEY idx_transfer_source (source_warehouse_id),
    KEY idx_transfer_destination (destination_warehouse_id),
    KEY idx_transfer_status (status),

    CONSTRAINT fk_transfer_destination
        FOREIGN KEY (destination_warehouse_id)
        REFERENCES warehouses (warehouse_id),

    CONSTRAINT fk_transfer_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT fk_transfer_source
        FOREIGN KEY (source_warehouse_id)
        REFERENCES warehouses (warehouse_id),

    CONSTRAINT chk_different_warehouses
        CHECK (source_warehouse_id <> destination_warehouse_id),

    CONSTRAINT chk_transfer_quantity
        CHECK (suggested_quantity > 0)
) ENGINE=InnoDB;


-- ============================================================
-- 9. MEDICINE CONSUMPTION
-- ============================================================

CREATE TABLE IF NOT EXISTS medicine_consumption (
    consumption_id BIGINT NOT NULL AUTO_INCREMENT,
    medicine_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    consumption_date DATE NOT NULL,
    consumed_quantity INT NOT NULL,
    reference_number VARCHAR(100) DEFAULT NULL,
    created_at TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (consumption_id),

    KEY idx_consumption_medicine (medicine_id),
    KEY idx_consumption_warehouse (warehouse_id),
    KEY idx_consumption_date (consumption_date),

    CONSTRAINT fk_consumption_medicine
        FOREIGN KEY (medicine_id)
        REFERENCES medicines (medicine_id),

    CONSTRAINT fk_consumption_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouses (warehouse_id),

    CONSTRAINT chk_consumed_quantity
        CHECK (consumed_quantity > 0)
) ENGINE=InnoDB;


-- ============================================================
-- END OF DATABASE CREATION
-- 8 TABLES CREATED
-- ============================================================
