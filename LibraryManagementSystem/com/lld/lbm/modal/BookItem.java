package com.lld.lbm.modal;



import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

import com.lld.lbm.enums.BookStatus;

/**
 * Represents a single physical copy of a {@link Book} in a specific branch.
 *
 * Thread-safety note: status transitions are protected by an item-level
 * {@link ReentrantLock} so concurrent borrow/return operations on the
 * SAME copy are serialised without blocking unrelated copies.
 */
public class BookItem {

    private final String     itemId;      // unique barcode / accession number
    private final Book       book;
    private final String     branchId;
    private volatile BookStatus status;

    // Fine-grained lock: one lock per physical copy
    private final ReentrantLock lock = new ReentrantLock();

    public BookItem(String itemId, Book book, String branchId) {
        this.itemId   = Objects.requireNonNull(itemId,   "Item ID cannot be null");
        this.book     = Objects.requireNonNull(book,     "Book cannot be null");
        this.branchId = Objects.requireNonNull(branchId, "Branch ID cannot be null");
        this.status   = BookStatus.AVAILABLE;
    }

    /** Try to atomically transition status; returns true only on success. */
    public boolean trySetStatus(BookStatus expected, BookStatus next) {
        lock.lock();
        try {
            if (status == expected) {
                status = next;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public String     getItemId()   { return itemId; }
    public Book       getBook()     { return book; }
    public String     getBranchId() { return branchId; }
    public BookStatus getStatus()   { return status; }

    @Override
    public String toString() {
        return String.format("BookItem{id='%s', book='%s', branch='%s', status=%s}",
                itemId, book.getTitle(), branchId, status);
    }
}