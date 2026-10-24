# FA-04 — Loans and Renewals

| Field | Value |
|---|---|
| Document ID | FA-04 |
| Version | 1.3 |
| Status | Approved |
| Owner | Tom Hollis, Business Analyst |
| Last updated | 2026-05-15 |
| Related | FA-02, FA-03, FA-05, FA-06, FA-07 |

## 1. Checkout

**UC-LOA-01 — Check out a copy at the desk**

1. The librarian scans the member card.
2. The librarian scans the copy barcode.
3. The system checks:
   - a. the membership is valid;
   - b. the member is not blocked: a member with an outstanding balance of more than €5.00 cannot borrow;
   - c. the maximum number of simultaneous loans is not reached (table below);
   - d. the copy has status AVAILABLE, or ON_HOLD for this member.
4. The system creates the loan, calculates the due date (§3) and sets the copy to ON_LOAN.
5. The member receives a receipt (printed or e-mail) listing all current loans and due dates.

Maximum number of simultaneous loans:

| Category | Maximum loans |
|---|---|
| ADULT | 10 |
| YOUTH | 8 |
| SENIOR | 10 |

If a check fails, the system shows the reason to the librarian. A Branch Manager cannot override checks a and b.

## 2. Loan periods

| Material type | Loan period (BR-LOA-01) |
|---|---|
| BOOK | 21 days |
| AUDIOVISUAL | 7 days |
| MAGAZINE | 14 days |

## 3. Due date

- Due date = checkout date + loan period.
- If the due date falls on a closing day (a Sunday or a public holiday), it moves to the next opening day (BR-LOA-02).
- Public holidays are listed in `06-reference/public-holidays-2026.csv` and maintained by the administrator in the system.

### 3.1 Worked examples

| # | Material | Checked out | Due date | Explanation |
|---|---|---|---|---|
| 1 | MAGAZINE | Wed 2026-03-11 | Wed 2026-03-25 | 14 days; the due date is an opening day |
| 2 | AUDIOVISUAL | Mon 2026-03-30 | Tue 2026-04-07 | 7 days gives Easter Monday 6 April, a public holiday; moved to the next opening day |
| 3 | BOOK | Sat 2026-04-11 | Sat 2026-05-02 | 21 days; Labour Day 1 May falls inside the loan period but does not move the due date |
| 4 | BOOK | Mon 2026-05-04 | Mon 2026-05-25 | 21 days; Ascension Day 14 May and Sunday 24 May fall inside the loan period but do not move the due date |
| 5 | AUDIOVISUAL | Tue 2026-07-14 | Wed 2026-07-22 | 7 days gives National Day 21 July, a public holiday; moved to the next opening day |
| 6 | MAGAZINE | Sat 2026-08-01 | Mon 2026-08-17 | 14 days gives Assumption Day 15 August, and 16 August is a Sunday; moved to Monday 17 August |

## 4. Renewals

**UC-LOA-02 — Renew a loan** (member portal or desk)

- A loan can be renewed at most **2 times** (BR-LOA-03).
- New due date = **current due date** + loan period of the material type, then adjusted to the next opening day if needed (BR-LOA-04).
- A renewal is refused when (BR-LOA-05):
  - the title has one or more waiting reservations;
  - the member is blocked or the membership has expired;
  - the loan is overdue;
  - the maximum number of renewals is reached.
- When a renewal is refused, the reason is shown to the member or librarian.

Example: a book is borrowed on Monday 2026-03-02 and is due on Monday 2026-03-23. The member renews on 2026-03-10. The new due date is 2026-03-23 + 21 days = Monday 2026-04-13.

## 5. Returns

**UC-LOA-03 — Return a copy**

- Copies can be returned at **any branch** (BR-LOA-06).
- The return date is the date on which the copy is checked in. Copies returned via an after-hours book drop are checked in with the date of the previous opening day.
- On return the system:
  1. closes the loan with the return date;
  2. calculates the overdue fine, if any, according to FA-06 §3, and adds it to the account as a charge;
  3. updates the copy status according to FA-03 §4.1;
  4. if a reservation is waiting, assigns the copy to that reservation (FA-05);
  5. gives the member a return receipt that shows any fine charged.

## 6. Overdue loans

- A loan is overdue from the first day after its due date.
- Overdue loans cannot be renewed; the copy must be returned.
- Overdue fines are described in FA-06 §3; notices are described in FA-07.
