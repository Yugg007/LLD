package com.lld.lbm.policy;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.lld.lbm.config.LibraryConfig;
import com.lld.lbm.modal.BorrowRecord;

/**
 * Default fine policy: ₹10 per overdue day.
 */
public class StandardFinePolicy implements FinePolicy {

    @Override
    public double calculateFine(BorrowRecord record, LocalDate returnDate) {
        long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
        if (overdueDays <= 0) return 0.0;
        return overdueDays * LibraryConfig.FINE_PER_DAY_RUPEES;
    }
}