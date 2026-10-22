#!/usr/bin/env python3
"""List the days between a due date and a return date and count the chargeable days (BR-FIN-01).

Usage: chargeable_days.py <due-date> <return-date>   (ISO dates, e.g. 2026-03-06 2026-03-13)

Chargeable days are opening days after the due date up to and including the return date.
Closing days are Sundays and the public holidays in shelfwise-analysis/06-reference/public-holidays-2026.csv.
"""
import csv
import sys
from datetime import date, timedelta
from pathlib import Path

HOLIDAYS = Path(__file__).resolve().parents[4] / "shelfwise-analysis" / "06-reference" / "public-holidays-2026.csv"


def load_holidays() -> dict[date, str]:
    with HOLIDAYS.open(newline="", encoding="utf-8") as f:
        return {date.fromisoformat(row["date"]): row["name"] for row in csv.DictReader(f)}


def main() -> None:
    if len(sys.argv) != 3:
        sys.exit(__doc__)
    due, end = date.fromisoformat(sys.argv[1]), date.fromisoformat(sys.argv[2])
    holidays = load_holidays()
    if any(d.year != 2026 for d in (due, end)):
        print("Warning: only the 2026 public holidays are known; other years count Sundays only.")
    count, day = 0, due + timedelta(days=1)
    while day <= end:
        if day.weekday() == 6:
            status = "closed (Sunday)"
        elif day in holidays:
            status = f"closed ({holidays[day]})"
        else:
            count += 1
            status = f"open, chargeable day {count}"
        print(f"{day.isoformat()} {day.strftime('%a')}  {status}")
        day += timedelta(days=1)
    print(f"Chargeable days: {count}")


if __name__ == "__main__":
    main()
