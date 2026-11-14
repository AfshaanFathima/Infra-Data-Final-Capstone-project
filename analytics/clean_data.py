
import pandas as pd
from pathlib import Path

# Import reusable cleaning functions
from cleaning_utils import (
    clean_text_columns,
    standardize_status_columns,
    convert_date_columns,
    inspect_duplicates,
    inspect_missing_values
)

# Project folders
PROJECT_DIR = Path(__file__).resolve().parent.parent
RAW_DIR = PROJECT_DIR / "data" / "raw"
CLEAN_DIR = PROJECT_DIR / "data" / "cleaned"

CLEAN_DIR.mkdir(parents=True, exist_ok=True)

# Current datasets
FILES = [
    "medicines.csv",
    "warehouses.csv",
    "inventory_batches.csv",
    "medicine_substitutes.csv",
    "reservations.csv",
    "reservation_allocations.csv",
    "stock_transfers.csv",
    "medicine_consumption.csv"
]

print("\n==============================================")
print("PHARMATRACK REUSABLE DATA CLEANING PIPELINE")
print("==============================================")

for file in FILES:

    print(f"\nProcessing: {file}")

    # Load raw dataset
    df = pd.read_csv(RAW_DIR / file)
    original_rows = len(df)

    # Step 1: Clean text columns
    df = clean_text_columns(df)

    # Step 2: Standardize status values
    df = standardize_status_columns(df)

    # Step 3: Convert date columns
    df = convert_date_columns(df)

    # Step 4: Inspect and remove duplicate rows
    duplicates = inspect_duplicates(df)
    df = df.drop_duplicates()

    # Step 5: Inspect missing values
    missing = inspect_missing_values(df)
    total_missing = int(missing.sum())

    # Save cleaned dataset
    output_path = CLEAN_DIR / file
    df.to_csv(output_path, index=False)

    # Display results
    print(f"Original rows: {original_rows}")
    print(f"Cleaned rows: {len(df)}")
    print(f"Duplicates removed: {duplicates}")
    print(f"Missing values: {total_missing}")
    print(f"Saved to: {output_path.name}")

print("\n==============================================")
print("ALL DATASETS PROCESSED SUCCESSFULLY!")
print("==============================================")
