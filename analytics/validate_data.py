
import pandas as pd
from pathlib import Path

DATA_DIR = Path(__file__).resolve().parent.parent / "data" / "cleaned"

tables = [
    "medicines",
    "warehouses",
    "inventory_batches",
    "medicine_substitutes",
    "reservations",
    "reservation_allocations",
    "stock_transfers",
    "medicine_consumption"
]

data = {}

print("\nPHARMATRACK DATA VALIDATION")
print("=" * 45)

# Load all cleaned CSV files
for table in tables:
    file_path = DATA_DIR / f"{table}.csv"

    if not file_path.exists():
        print(f"MISSING FILE: {table}.csv")
        continue

    data[table] = pd.read_csv(file_path)
    print(f"LOADED: {table} - {len(data[table])} records")


# Check that all files are available
if len(data) != len(tables):
    print("\nValidation stopped: Some CSV files are missing.")
    raise SystemExit(1)


errors = 0
warnings = 0


def check(condition, message):
    global errors
    if condition:
        print(f"PASS: {message}")
    else:
        print(f"FAIL: {message}")
        errors += 1


def check_reference(df, column, parent_df, parent_column, label):
    invalid = ~df[column].isin(parent_df[parent_column])
    check(not invalid.any(), f"{label}: valid references")


# Primary key checks
for table, df in data.items():
    pk = {
        "medicines": "medicine_id",
        "warehouses": "warehouse_id",
        "inventory_batches": "batch_id",
        "medicine_substitutes": "substitution_id",
        "reservations": "reservation_id",
        "reservation_allocations": "allocation_id",
        "stock_transfers": "transfer_id",
        "medicine_consumption": "consumption_id"
    }[table]

    check(df[pk].notna().all(), f"{table}: primary keys are present")
    check(not df[pk].duplicated().any(), f"{table}: no duplicate primary keys")


# Foreign key checks
check_reference(data["inventory_batches"], "medicine_id",
                data["medicines"], "medicine_id", "Batch medicine")

check_reference(data["inventory_batches"], "warehouse_id",
                data["warehouses"], "warehouse_id", "Batch warehouse")

check_reference(data["medicine_substitutes"], "medicine_id",
                data["medicines"], "medicine_id", "Substitution medicine")

check_reference(data["medicine_substitutes"], "substitute_medicine_id",
                data["medicines"], "medicine_id", "Substitute medicine")

check_reference(data["reservations"], "medicine_id",
                data["medicines"], "medicine_id", "Reservation medicine")

check_reference(data["reservation_allocations"], "reservation_id",
                data["reservations"], "reservation_id", "Allocation reservation")

check_reference(data["reservation_allocations"], "batch_id",
                data["inventory_batches"], "batch_id", "Allocation batch")

check_reference(data["stock_transfers"], "medicine_id",
                data["medicines"], "medicine_id", "Transfer medicine")

check_reference(data["stock_transfers"], "source_warehouse_id",
                data["warehouses"], "warehouse_id", "Source warehouse")

check_reference(data["stock_transfers"], "destination_warehouse_id",
                data["warehouses"], "warehouse_id", "Destination warehouse")

check_reference(data["medicine_consumption"], "medicine_id",
                data["medicines"], "medicine_id", "Consumption medicine")

check_reference(data["medicine_consumption"], "warehouse_id",
                data["warehouses"], "warehouse_id", "Consumption warehouse")


# Quantity checks
batches = data["inventory_batches"]

check(
    (batches["quantity_on_hand"] >= 0).all()
    and (batches["quantity_reserved"] >= 0).all()
    and (batches["quantity_quarantined"] >= 0).all()
    and (
        batches["quantity_reserved"] + batches["quantity_quarantined"]
        <= batches["quantity_on_hand"]
    ).all(),
    "Inventory batch quantities are valid"
)

check(
    (data["reservations"]["requested_quantity"] > 0).all()
    and (
        data["reservations"]["allocated_quantity"]
        <= data["reservations"]["requested_quantity"]
    ).all(),
    "Reservation quantities are valid"
)

check(
    (data["reservation_allocations"]["allocated_quantity"] > 0).all(),
    "Allocation quantities are positive"
)

check(
    (data["stock_transfers"]["suggested_quantity"] > 0).all(),
    "Transfer quantities are positive"
)

check(
    (data["medicine_consumption"]["consumed_quantity"] > 0).all(),
    "Consumption quantities are positive"
)


# Business rule checks
substitutes = data["medicine_substitutes"]

check(
    (substitutes["medicine_id"] != substitutes["substitute_medicine_id"]).all(),
    "Medicines are not substituted with themselves"
)

transfers = data["stock_transfers"]

check(
    (transfers["source_warehouse_id"] !=
     transfers["destination_warehouse_id"]).all(),
    "Transfer source and destination are different"
)


# Check allocation totals against batch reserved quantities
allocation_totals = (
    data["reservation_allocations"]
    .groupby("batch_id")["allocated_quantity"]
    .sum()
)

for batch_id, allocated in allocation_totals.items():
    batch = batches.loc[batches["batch_id"] == batch_id]

    if not batch.empty:
        reserved = batch.iloc[0]["quantity_reserved"]

        if allocated != reserved:
            warnings += 1
            print(
                f"WARNING: Batch {batch_id}: "
                f"reserved quantity is {reserved}, "
                f"but allocations total {allocated}"
            )


print("\n" + "=" * 45)
print(f"VALIDATION ERRORS: {errors}")
print(f"DATA WARNINGS: {warnings}")

if errors == 0:
    print("VALIDATION COMPLETED")
else:
    print("VALIDATION FOUND ERRORS")
