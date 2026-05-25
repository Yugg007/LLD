package com.lld.lbm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lld.lbm.policy.FinePolicy;
import com.lld.lbm.policy.StandardFinePolicy;
import com.lld.lbm.repository.BorrowRecordRepository;
import com.lld.lbm.repository.LibraryRepository;
import com.lld.lbm.repository.MemberRepository;
import com.lld.lbm.search.BookSearchEngine;
import com.lld.lbm.service.BorrowingService;
import com.lld.lbm.service.InventoryService;
import com.lld.lbm.service.LibraryService;
import com.lld.lbm.service.MemberService;

/**
 * Spring configuration for dependency injection and bean management.
 * All beans are defined here to ensure loose coupling and testability.
 */
@Configuration
public class AppConfig {

    // ── Repositories ──────────────────────────────────────────────────────────

    @Bean
    public LibraryRepository libraryRepository() {
        return new LibraryRepository();
    }

    @Bean
    public MemberRepository memberRepository() {
        return new MemberRepository();
    }

    @Bean
    public BorrowRecordRepository borrowRecordRepository() {
        return new BorrowRecordRepository();
    }

    // ── Policies ──────────────────────────────────────────────────────────────

    @Bean
    public FinePolicy finePolicy() {
        // Swap to HolidayFinePolicy for weekend-aware charging
        return new StandardFinePolicy();
    }

    // ── Services ──────────────────────────────────────────────────────────────

    @Bean
    public InventoryService inventoryService(LibraryRepository libraryRepo) {
        return new InventoryService(libraryRepo);
    }

    @Bean
    public MemberService memberService(MemberRepository memberRepo) {
        return new MemberService(memberRepo);
    }

    @Bean
    public BorrowingService borrowingService(LibraryRepository libraryRepo,
                                              MemberRepository memberRepo,
                                              BorrowRecordRepository recordRepo,
                                              FinePolicy finePolicy) {
        return new BorrowingService(libraryRepo, memberRepo, recordRepo, finePolicy);
    }

    @Bean
    public LibraryService libraryService(MemberRepository memberRepo,
                                          BorrowRecordRepository recordRepo) {
        return new LibraryService(memberRepo, recordRepo);
    }

    @Bean
    public BookSearchEngine bookSearchEngine() {
        return new BookSearchEngine();
    }

}
