package com.lld.lbm.service;


import java.time.LocalDate;
import java.util.UUID;

import com.lld.lbm.enums.BookStatus;
import com.lld.lbm.exception.LibraryException;
import com.lld.lbm.modal.BookItem;
import com.lld.lbm.modal.BorrowRecord;
import com.lld.lbm.modal.Member;
import com.lld.lbm.policy.FinePolicy;
import com.lld.lbm.repository.BorrowRecordRepository;
import com.lld.lbm.repository.LibraryRepository;
import com.lld.lbm.repository.MemberRepository;

/**
 * Orchestrates the borrow/return lifecycle.
 *
 * ─── Thread-Safety Strategy ─────────────────────────────────────────────────
 *
 *  We need TWO state changes to be atomic per operation:
 *    borrow:  BookItem.status  AVAILABLE → BORROWED
 *             Member.activeItemIds += itemId
 *
 *    return:  BookItem.status  BORROWED  → AVAILABLE
 *             Member.activeItemIds -= itemId
 *
 *  Approach: fine-grained locking on the BookItem (via trySetStatus), combined
 *  with a per-member synchronised block for the member count check+update.
 *
 *  This avoids a single global lock that would serialise all operations.
 *  - Two threads borrowing DIFFERENT books proceed in parallel ✓
 *  - Two threads borrowing the SAME copy: only one wins the CAS ✓
 *  - Two threads borrowing DIFFERENT books for the SAME member:
 *      synchronised(member) ensures the limit check + add is atomic ✓
 *
 * ────────────────────────────────────────────────────────────────────────────
 */
public class BorrowingService {

    private final LibraryRepository      libraryRepo;
    private final MemberRepository       memberRepo;
    private final BorrowRecordRepository recordRepo;
    private final FinePolicy             finePolicy;

    public BorrowingService(LibraryRepository libraryRepo,
                            MemberRepository memberRepo,
                            BorrowRecordRepository recordRepo,
                            FinePolicy finePolicy) {
        this.libraryRepo = libraryRepo;
        this.memberRepo  = memberRepo;
        this.recordRepo  = recordRepo;
        this.finePolicy  = finePolicy;
    }

    // ── Borrow ────────────────────────────────────────────────────────────────

    /**
     * Borrow a specific book item for a member.
     *
     * @return the created {@link BorrowRecord}
     * @throws LibraryException if the item is unavailable or the member is at limit
     */
    public BorrowRecord borrowBook(String memberId, String branchId, String itemId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new LibraryException("Member not found: " + memberId));

        BookItem item = libraryRepo.findById(branchId)
                .orElseThrow(() -> new LibraryException("Branch not found: " + branchId))
                .getBookItem(itemId);

        if (item == null) {
            throw new LibraryException("Book item not found: " + itemId + " in branch " + branchId);
        }

        // Step 1 — atomically reserve the book copy (CAS on the item-level lock)
        boolean reserved = item.trySetStatus(BookStatus.AVAILABLE, BookStatus.BORROWED);
        if (!reserved) {
            throw new LibraryException(
                    "Book item '" + itemId + "' is not available (status=" + item.getStatus() + ")");
        }

        // Step 2 — atomically check + update the member's borrow count
        //          synchronise on the MEMBER object to prevent concurrent borrows
        //          from the same account bypassing the limit check
        synchronized (member) {
            if (!member.canBorrow()) {
                // Roll back: release the item we just reserved
                item.trySetStatus(BookStatus.BORROWED, BookStatus.AVAILABLE);
                throw new LibraryException(
                        "Member '" + memberId + "' has reached the borrow limit of "
                        + member.getMaxBorrowLimit());
            }
            member.addActiveBorrow(itemId);
        }

        // Step 3 — persist the record (outside the lock; pure write to concurrent map)
        LocalDate today   = LocalDate.now();
        LocalDate dueDate = today.plusDays(member.getLoanDurationDays());

        BorrowRecord record = new BorrowRecord(
                UUID.randomUUID().toString(),
                memberId, itemId, today, dueDate);

        recordRepo.save(record);

        System.out.printf("[BORROW] Member '%s' borrowed '%s' — due %s%n",
                memberId, item.getBook().getTitle(), dueDate);

        return record;
    }

    // ── Return ────────────────────────────────────────────────────────────────

    /**
     * Return a borrowed book item.
     *
     * @return fine amount in ₹ (0 if on time)
     */
    public double returnBook(String memberId, String branchId, String itemId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new LibraryException("Member not found: " + memberId));

        BookItem item = libraryRepo.findById(branchId)
                .orElseThrow(() -> new LibraryException("Branch not found: " + branchId))
                .getBookItem(itemId);

        if (item == null) {
            throw new LibraryException("Book item not found: " + itemId);
        }

        // Step 1 — atomically release the item
        boolean released = item.trySetStatus(BookStatus.BORROWED, BookStatus.AVAILABLE);
        if (!released) {
            throw new LibraryException(
                    "Book item '" + itemId + "' was not in BORROWED state (status=" + item.getStatus() + ")");
        }

        // Step 2 — update member record (thread-safe set, no lock needed)
        member.removeActiveBorrow(itemId);

        // Step 3 — close the borrow record and calculate fine
        LocalDate today = LocalDate.now();
        BorrowRecord record = recordRepo.findActiveByItemId(itemId)
                .orElseThrow(() -> new LibraryException("No active borrow record for item: " + itemId));

        record.markReturned(today);

        double fine = finePolicy.calculateFine(record, today);

        System.out.printf("[RETURN] Member '%s' returned '%s' — fine: ₹%.2f%n",
                memberId, item.getBook().getTitle(), fine);

        return fine;
    }
}
