package com.lld.lbm.demo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.lld.lbm.modal.Book;
import com.lld.lbm.modal.Library;
import com.lld.lbm.policy.HolidayFinePolicy;
import com.lld.lbm.policy.StandardFinePolicy;
import com.lld.lbm.repository.BorrowRecordRepository;
import com.lld.lbm.repository.LibraryRepository;
import com.lld.lbm.repository.MemberRepository;
import com.lld.lbm.search.BookSearchEngine;
import com.lld.lbm.search.SearchCriteriaFactory;
import com.lld.lbm.service.BorrowingService;
import com.lld.lbm.service.InventoryService;
import com.lld.lbm.service.MemberService;

/**
 * End-to-end demonstration of the Library Management System.
 *
 * Covers:
 *  Branch & inventory setup
 *  Member registration (Standard + Premium)
 *  Book search (by title, author, year, composite)
 *  Borrow / return lifecycle
 *  Fine calculation (standard policy)
 *  Concurrency test — 10 threads race to borrow the same copy
 *  Borrow-limit enforcement
 */
public class LibraryDemo {

    public static void main(String[] args) throws InterruptedException {

        // ── Wiring (manual DI — swap any component without touching others) ──
        LibraryRepository      libraryRepo = new LibraryRepository();
        MemberRepository       memberRepo  = new MemberRepository();
        BorrowRecordRepository recordRepo  = new BorrowRecordRepository();

        InventoryService inventoryService = new InventoryService(libraryRepo);
        MemberService    memberService    = new MemberService(memberRepo);

        // Use StandardFinePolicy; replace with HolidayFinePolicy to change behaviour
        BorrowingService borrowingService = new BorrowingService(
                libraryRepo, memberRepo, recordRepo, new StandardFinePolicy());

        BookSearchEngine searchEngine = new BookSearchEngine();

        // ── 1. Branches ───────────────────────────────────────────────────────
        separator("1. BRANCH SETUP");
        inventoryService.addBranch("BR-001", "Central Library",    "MG Road, Mumbai");
        inventoryService.addBranch("BR-002", "Bandra Branch",      "Hill Road, Bandra");

        // ── 2. Books & copies ─────────────────────────────────────────────────
        separator("2. INVENTORY");
        Book dune        = new Book("978-0-441-17271-9", "Dune",              "Frank Herbert",     1965);
        Book foundation  = new Book("978-0-553-29335-7", "Foundation",        "Isaac Asimov",      1951);
        Book lotr        = new Book("978-0-618-64015-7", "The Lord of the Rings", "J.R.R. Tolkien", 1954);
        Book hitchhiker  = new Book("978-0-330-25864-5", "The Hitchhiker's Guide", "Douglas Adams", 1979);

        // Central branch gets 2 copies of Dune, 1 Foundation, 1 LOTR
        inventoryService.addBookItem("ITEM-001", dune,       "BR-001");
        inventoryService.addBookItem("ITEM-002", dune,       "BR-001");
        inventoryService.addBookItem("ITEM-003", foundation, "BR-001");
        inventoryService.addBookItem("ITEM-004", lotr,       "BR-001");

        // Bandra gets 1 Hitchhiker, 1 Foundation
        inventoryService.addBookItem("ITEM-005", hitchhiker, "BR-002");
        inventoryService.addBookItem("ITEM-006", foundation, "BR-002");

        // ── 3. Members ────────────────────────────────────────────────────────
        separator("3. MEMBERS");
        memberService.registerStandardMember("M-001", "Alice");
        memberService.registerStandardMember("M-002", "Bob");
        memberService.registerPremiumMember ("M-003", "Charlie");

        // ── 4. Search ─────────────────────────────────────────────────────────
        separator("4. BOOK SEARCH");
        List<Library> allBranches = (List<Library>) inventoryService.getAllBranches();

        System.out.println("\n-- Search by Title 'dune' --");
        searchEngine.search(allBranches, SearchCriteriaFactory.byTitle("dune"))
                .forEach(System.out::println);

        System.out.println("\n-- Search by Author 'Asimov' --");
        searchEngine.search(allBranches, SearchCriteriaFactory.byAuthor("Asimov"))
                .forEach(System.out::println);

        System.out.println("\n-- Search by Year 1954 --");
        searchEngine.search(allBranches, SearchCriteriaFactory.byPublicationYear(1954))
                .forEach(System.out::println);

        System.out.println("\n-- Composite: Author 'Herbert' AND Year 1965 --");
        searchEngine.search(allBranches,
                        SearchCriteriaFactory.and(
                                SearchCriteriaFactory.byAuthor("Herbert"),
                                SearchCriteriaFactory.byPublicationYear(1965)))
                .forEach(System.out::println);

        System.out.println("\n-- Available copies only for 'Foundation' (all branches) --");
        searchEngine.searchAvailable(allBranches, SearchCriteriaFactory.byTitle("Foundation"))
                .forEach(System.out::println);

        // ── 5. Normal borrow & return ─────────────────────────────────────────
        separator("5. BORROW & RETURN");
        borrowingService.borrowBook("M-001", "BR-001", "ITEM-001");
        borrowingService.borrowBook("M-001", "BR-001", "ITEM-003");
        borrowingService.borrowBook("M-002", "BR-002", "ITEM-005");

        double fine = borrowingService.returnBook("M-001", "BR-001", "ITEM-001");
        System.out.println("Fine for on-time return: ₹" + fine);   // ₹0

        // ── 6. Borrow-limit enforcement ───────────────────────────────────────
        separator("6. BORROW LIMIT (Standard = 5)");
        Book[] extras = {
            new Book("X-001", "Book A", "Author X", 2020),
            new Book("X-002", "Book B", "Author X", 2020),
            new Book("X-003", "Book C", "Author X", 2020),
            new Book("X-004", "Book D", "Author X", 2020),
        };
        for (int i = 0; i < 4; i++) {
            inventoryService.addBookItem("XITEM-00" + i, extras[i], "BR-001");
            try {
                borrowingService.borrowBook("M-001", "BR-001", "XITEM-00" + i);
            } catch (Exception e) {
                System.out.println("Expected rejection: " + e.getMessage());
            }
        }

        // ── 7. Concurrency test ───────────────────────────────────────────────
        separator("7. CONCURRENCY — 10 threads race for ITEM-002 (only 1 should win)");

        // Return ITEM-001 so we can reuse member M-001 slot; also borrow fresh
        // Reset: Alice currently holds ITEM-003 + some extras let's use Bob
        // Return Bob's book first so he has capacity
        borrowingService.returnBook("M-002", "BR-002", "ITEM-005");

        int threadCount = 10;
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        CopyOnWriteArrayList<String> results = new CopyOnWriteArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            final String memberId = "M-00" + ((i % 2) + 1); // alternates M-001 / M-002
            pool.submit(() -> {
                try {
                    latch.await(); // all threads start simultaneously
                    borrowingService.borrowBook(memberId, "BR-001", "ITEM-002");
                    results.add("SUCCESS: " + memberId + " borrowed ITEM-002");
                } catch (Exception e) {
                    results.add("REJECTED: " + e.getMessage());
                }
            });
        }

        latch.countDown(); // fire!
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);

        long successCount = results.stream().filter(r -> r.startsWith("SUCCESS")).count();
        System.out.println("\nResults (" + threadCount + " threads):");
        results.forEach(r -> System.out.println("  " + r));
        System.out.println("\nSuccessful borrows (must be exactly 1): " + successCount);

        // ── 8. Fine with HolidayFinePolicy (pluggable) ───────────────────────
        separator("8. EXTENSIBILITY — HolidayFinePolicy");
        BorrowingService holidayService = new BorrowingService(
                libraryRepo, memberRepo, recordRepo, new HolidayFinePolicy());
        System.out.println("HolidayFinePolicy injected — weekends are not charged.");
        System.out.println("Zero code changes to BorrowingService or any domain model.");
    }

    private static void separator(String title) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + title);
        System.out.println("═".repeat(60));
    }
}