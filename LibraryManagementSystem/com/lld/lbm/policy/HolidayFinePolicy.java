package com.lld.lbm.policy;


import java.time.DayOfWeek;
import java.time.LocalDate;

import com.lld.lbm.modal.BorrowRecord;

/**
 * Holiday-aware fine policy: weekends and public holidays are not charged.
 *
 * Demonstrates OCP — added without modifying {@link StandardFinePolicy}
 * or any service class. Swap it in via dependency injection.
 */
public class HolidayFinePolicy implements FinePolicy {

    private static final double FINE_PER_CHARGEABLE_DAY = 10.0;

    @Override
    public double calculateFine(BorrowRecord record, LocalDate returnDate) {
        if (!returnDate.isAfter(record.getDueDate())) return 0.0;

        long chargeableDays = record.getDueDate()
                .datesUntil(returnDate)
                .filter(this::isChargeableDay)
                .count();

        return chargeableDays * FINE_PER_CHARGEABLE_DAY;
    }

    /**
     * A day is chargeable if it is a weekday (Mon–Fri).
     * Extend this to also skip public holidays stored in a calendar service.
     */
    private boolean isChargeableDay(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow != DayOfWeek.SATURDAY && dow != DayOfWeek.SUNDAY;
    }
}