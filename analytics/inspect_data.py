
import pandas as pd
from pathlib import Path

# Project directory
PROJECT_DIR = Path(__file__).resolve().parent.parent
RAW_DIR = PROJECT_DIR / "data" / "raw"

# List of CSV files
files = [
    "medicines.csv",
    "warehouses.csv",
    "inventory_batches.csv",
    "medicine_substitutes.csv",
    "reservations.csv",
    "reservation_allocations.csv",
    "stock_transfers.csv",
    "medicine_consumption.csv"
]

# Inspect each dataset
for file in files:

    print("\n" + "=" * 60)
    print("DATASET:", file)
    print("=" * 60)

    df = pd.read_csv(RAW_DIR / file)

    print("\n1. FIRST FIVE ROWS")
    print(df.head())

    print("\n2. LAST FIVE ROWS")
    print(df.tail())

    print("\n3. DATASET SHAPE")
    print(df.shape)

    print("\n4. COLUMN NAMES")
    print(df.columns.tolist())

    print("\n5. DATA TYPES AND NON-NULL COUNTS")
    df.info()

    print("\n6. STATISTICAL SUMMARY")
    print(df.describe(include="all"))

    print("\n7. MISSING VALUES")
    print(df.isnull().sum())

    print("\n8. DUPLICATE ROWS")
    print(df.duplicated().sum())

    print("\n9. UNIQUE VALUE COUNTS")
    for column in df.columns:
        print(column, ":", df[column].nunique())
