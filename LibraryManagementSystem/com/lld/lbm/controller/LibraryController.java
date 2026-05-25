package com.lld.lbm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lld.lbm.modal.BorrowRecord;
import com.lld.lbm.service.BorrowingService;
import com.lld.lbm.service.InventoryService;
import com.lld.lbm.service.LibraryService;
import com.lld.lbm.service.MemberService;

/**
 * REST API for the Library Management System.
 * Exposes endpoints for:
 *  - Member management
 *  - Book borrowing and returning
 *  - Book search
 *  - Library operations
 */
@RestController
@RequestMapping("/api/library")
public class LibraryController {

    private final InventoryService inventoryService;
    private final MemberService memberService;
    private final BorrowingService borrowingService;
    private final LibraryService libraryService;

    public LibraryController(InventoryService inventoryService,
                             MemberService memberService,
                             BorrowingService borrowingService,
                             LibraryService libraryService) {
        this.inventoryService = inventoryService;
        this.memberService = memberService;
        this.borrowingService = borrowingService;
        this.libraryService = libraryService;
    }

    // ── Member Endpoints ──────────────────────────────────────────────────────

    @PostMapping("/members/standard")
    public String registerStandardMember(
            @RequestParam String memberId,
            @RequestParam String name) {
        memberService.registerStandardMember(memberId, name);
        return "Standard member registered: " + memberId;
    }

    @PostMapping("/members/premium")
    public String registerPremiumMember(
            @RequestParam String memberId,
            @RequestParam String name) {
        memberService.registerPremiumMember(memberId, name);
        return "Premium member registered: " + memberId;
    }

    @GetMapping("/members/{memberId}")
    public String getMemberSummary(@PathVariable String memberId) {
        return libraryService.getMemberSummary(memberId);
    }

    // ── Branch Endpoints ──────────────────────────────────────────────────────

    @PostMapping("/branches")
    public String addBranch(
            @RequestParam String branchId,
            @RequestParam String name,
            @RequestParam String address) {
        inventoryService.addBranch(branchId, name, address);
        return "Branch added: " + branchId;
    }

    @GetMapping("/branches")
    public String getAllBranches() {
        return inventoryService.getAllBranches().toString();
    }

    // ── Borrowing Endpoints ───────────────────────────────────────────────────

    @PostMapping("/borrow")
    public String borrowBook(
            @RequestParam String memberId,
            @RequestParam String branchId,
            @RequestParam String itemId) {
        BorrowRecord record = borrowingService.borrowBook(memberId, branchId, itemId);
        return "Book borrowed successfully. Due date: " + record.getDueDate();
    }

    @PostMapping("/return")
    public String returnBook(
            @RequestParam String memberId,
            @RequestParam String branchId,
            @RequestParam String itemId) {
        double fine = borrowingService.returnBook(memberId, branchId, itemId);
        return "Book returned. Fine: ₹" + fine;
    }

    @GetMapping("/members/{memberId}/history")
    public String getMemberHistory(@PathVariable String memberId) {
        return libraryService.getMemberBorrowHistory(memberId).toString();
    }

}
