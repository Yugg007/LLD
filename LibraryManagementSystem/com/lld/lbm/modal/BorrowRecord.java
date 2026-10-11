package com.lld.lbm.modal;

import java.time.LocalDate;

/**
 * Immutable snapshot of a borrowing transaction.
 * A new record is created for every borrow; returnDate is set on return.
 */
public class BorrowRecord {

    private final String    recordId;
    private final String    memberId;
    private final String    bookItemId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private volatile LocalDate returnDate;   // null until returned

    public BorrowRecord(String recordId,
                        String memberId,
                        String bookItemId,
                        LocalDate borrowDate,
                        LocalDate dueDate) {
        this.recordId   = recordId;
        this.memberId   = memberId;
        this.bookItemId = bookItemId;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
    }

    public void markReturned(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned()      { return returnDate != null; }

    public String    getRecordId()   { return recordId; }
    public String    getMemberId()   { return memberId; }
    public String    getBookItemId() { return bookItemId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate()    { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }

    @Override
    public String toString() {
        return String.format(
                "BorrowRecord{id='%s', member='%s', item='%s', due=%s, returned=%s}",
                recordId, memberId, bookItemId, dueDate,
                returnDate != null ? returnDate : "NOT_RETURNED");
    }
}