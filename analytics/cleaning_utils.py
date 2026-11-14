
import pandas as pd


def clean_text_columns(df):
    """Remove unnecessary spaces and standardize blank text."""
    for column in df.columns:
        if df[column].dtype == "str" or df[column].dtype == "object":
            df[column] = df[column].str.strip()
            df[column] = df[column].replace("", pd.NA)

    return df


def standardize_status_columns(df):
    """Convert status values to uppercase."""
    for column in df.columns:
        if "status" in column.lower():
            df[column] = df[column].str.upper()

    return df


def convert_date_columns(df):
    """Convert columns with date/time names into datetime values."""
    for column in df.columns:
        name = column.lower()

        if (
            name.endswith("_date")
            or name.endswith("_at")
            or name in ["date", "datetime"]
        ):
            df[column] = pd.to_datetime(
                df[column], errors="coerce"
            )

    return df


def inspect_duplicates(df):
    """Report duplicate rows without deleting them."""
    return int(df.duplicated().sum())


def inspect_missing_values(df):
    """Return missing-value counts for each column."""
    return df.isnull().sum()
