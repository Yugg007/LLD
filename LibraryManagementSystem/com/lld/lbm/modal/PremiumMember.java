package com.lld.lbm.modal;

import com.lld.lbm.config.LibraryConfig;
import com.lld.lbm.enums.MemberType;

/**
 * Premium member with higher borrowing limits and longer loan periods.
 *
 * Added purely by extending {@link Member} — zero changes to existing classes.
 * This demonstrates adherence to the Open/Closed Principle.
 */
public class PremiumMember extends Member {

    public PremiumMember(String memberId, String name) {
        super(memberId, name, MemberType.PREMIUM);
    }

    @Override
    public int getMaxBorrowLimit() {
        return LibraryConfig.PREMIUM_MEMBER_BORROW_LIMIT;
    }

    @Override
    public int getLoanDurationDays() {
        return LibraryConfig.PREMIUM_LOAN_DURATION_DAYS;
    }
}