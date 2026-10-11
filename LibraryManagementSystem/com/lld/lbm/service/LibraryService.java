package com.lld.lbm.service;

import java.util.List;
import java.util.stream.Collectors;

import com.lld.lbm.modal.BorrowRecord;
import com.lld.lbm.modal.Member;
import com.lld.lbm.repository.BorrowRecordRepository;
import com.lld.lbm.repository.MemberRepository;

/**
 * High-level library operations: reporting, history, member insights.
 * Aggregates data from repositories to provide useful views.
 */
public class LibraryService {

    private final MemberRepository memberRepo;
    private final BorrowRecordRepository recordRepo;

    public LibraryService(MemberRepository memberRepo,
                          BorrowRecordRepository recordRepo) {
        this.memberRepo = memberRepo;
        this.recordRepo = recordRepo;
    }

    /**
     * Get all current borrows for a member.
     */
    public List<BorrowRecord> getMemberBorrowHistory(String memberId) {
        return recordRepo.findByMemberId(memberId);
    }

    /**
     * Get a summary of a member's activity.
     */
    public String getMemberSummary(String memberId) {
        Member member = memberRepo.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found: " + memberId));

        List<BorrowRecord> history = recordRepo.findByMemberId(memberId);
        long totalBorrows = history.size();
        long returnedCount = history.stream().filter(BorrowRecord::isReturned).count();
        double totalFines = history.stream()
                .filter(BorrowRecord::isReturned)
                .mapToDouble(r -> 0.0) // Fine calculation would happen here if passed
                .sum();

        return String.format(
                "Member Summary{id='%s', name='%s', type=%s, totalBorrows=%d, returned=%d, active=%d}",
                member.getMemberId(), member.getName(), member.getMemberType(),
                totalBorrows, returnedCount, member.getActiveBorrowCount());
    }

    /**
     * Get all members in the system.
     */
    public List<Member> getAllMembers() {
        return memberRepo.findAll().stream().collect(Collectors.toList());
    }

}
