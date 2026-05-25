package com.lld.lbm.modal;

import com.lld.lbm.config.LibraryConfig;
import com.lld.lbm.enums.MemberType;

/**
 * Standard library member with default borrowing privileges.
 */
public class StandardMember extends Member {

    public StandardMember(String memberId, String name) {
        super(memberId, name, MemberType.STANDARD);
    }

    @Override
    public int getMaxBorrowLimit() {
        return LibraryConfig.STANDARD_MEMBER_BORROW_LIMIT;
    }

    @Override
    public int getLoanDurationDays() {
        return LibraryConfig.STANDARD_LOAN_DURATION_DAYS;
    }
}