
import mysql.connector
import pandas as pd
from pathlib import Path
from getpass import getpass

# PHARMATRACK - MYSQL TO CSV EXPORT

# Database connection settings
DB_CONFIG = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "database": "pharma_inventory_db"
}

# Project folders
PROJECT_DIR = Path(__file__).resolve().parent.parent
RAW_DIR = PROJECT_DIR / "data" / "raw"

# Create raw folder if it does not exist
RAW_DIR.mkdir(parents=True, exist_ok=True)

# Actual operational tables
TABLES = [
    "medicines",
    "warehouses",
    "inventory_batches",
    "medicine_substitutes",
    "reservations",
    "reservation_allocations",
    "stock_transfers",
    "medicine_consumption"
]


def export_tables():
    password = getpass("Enter your MySQL root password: ")

    connection = None

    try:
        connection = mysql.connector.connect(
            **DB_CONFIG,
            password=password
        )

        print("\nConnected to MySQL successfully!")
        print("Database:", DB_CONFIG["database"])

        for table in TABLES:
            query = f"SELECT * FROM `{table}`"

            df = pd.read_sql(query, connection)

            output_file = RAW_DIR / f"{table}.csv"

            df.to_csv(output_file, index=False)

            print(
                f"Exported {table}: "
                f"{len(df)} records -> {output_file.name}"
            )

        print("\nAll eight tables exported successfully!")

    except mysql.connector.Error as error:
        print("\nMySQL connection or query error:", error)

    except Exception as error:
        print("\nExport error:", error)

    finally:
        if connection and connection.is_connected():
            connection.close()
            print("\nMySQL connection closed.")


if __name__ == "__main__":
    export_tables()
