package com.lld.lbm.policy;


import java.time.LocalDate;

import com.lld.lbm.modal.BorrowRecord;

/**
 * Strategy interface for fine calculation.
 *
 * New policies (HolidayFinePolicy, WaiverPolicy) are added by
 * implementing this interface — existing code stays untouched.
 */
public interface FinePolicy {
    /**
     * Calculate the fine for a returned book.
     *
     * @param record     the completed borrow record
     * @param returnDate actual date of return
     * @return fine amount in ₹ (0 if not overdue)
     */
    double calculateFine(BorrowRecord record, LocalDate returnDate);
}