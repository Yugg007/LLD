package com.lld.lbm.modal;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.lld.lbm.enums.MemberType;

/**
 * Base class for library members.
 *
 * Subclasses define borrowing limits and loan durations via template methods,
 * satisfying OCP: add PremiumMember without touching this class.
 *
 * activeItemIds is a thread-safe set so concurrent borrow/return operations
 * on the same member do not corrupt the count.
 */
public abstract class Member {

    private final String     memberId;
    private final String     name;
    private final MemberType memberType;

    // ConcurrentHashMap.newKeySet() gives a thread-safe Set
    private final Set<String> activeItemIds = ConcurrentHashMap.newKeySet();

    protected Member(String memberId, String name, MemberType memberType) {
        this.memberId   = memberId;
        this.name       = name;
        this.memberType = memberType;
    }

    // ── Template methods ─────────────────────────────────────────────────────

    /** Maximum number of books this member type may borrow simultaneously. */
    public abstract int getMaxBorrowLimit();

    /** Loan duration in days for this member type. */
    public abstract int getLoanDurationDays();

    // ── Borrow tracking ──────────────────────────────────────────────────────

    public boolean canBorrow() {
        return activeItemIds.size() < getMaxBorrowLimit();
    }

    public void addActiveBorrow(String itemId) {
        activeItemIds.add(itemId);
    }

    public void removeActiveBorrow(String itemId) {
        activeItemIds.remove(itemId);
    }

    public int getActiveBorrowCount() {
        return activeItemIds.size();
    }

    public Set<String> getActiveItemIds() {
        return Collections.unmodifiableSet(activeItemIds);
    }

    // ── Accessors ─────────────────────────────────────────────────────────────

    public String     getMemberId()   { return memberId; }
    public String     getName()       { return name; }
    public MemberType getMemberType() { return memberType; }

    @Override
    public String toString() {
        return String.format("Member{id='%s', name='%s', type=%s, activeBorrows=%d/%d}",
                memberId, name, memberType, activeItemIds.size(), getMaxBorrowLimit());
    }
}