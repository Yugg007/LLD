package com.lld.lbm.search;



import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.lld.lbm.enums.BookStatus;
import com.lld.lbm.modal.BookItem;
import com.lld.lbm.modal.Library;

/**
 * Stateless search engine. Accepts any {@link SearchCriteria} and filters
 * the provided inventory.
 *
 * This class is CLOSED for modification but OPEN for extension through
 * new {@link SearchCriteria} implementations.
 */
public class BookSearchEngine {

    /**
     * Search across all items in a single branch.
     */
    public List<BookItem> search(Library library, SearchCriteria criteria) {
        return library.getAllItems().stream()
                .filter(item -> criteria.matches(item.getBook()))
                .collect(Collectors.toList());
    }

    /**
     * Search across multiple branches simultaneously.
     */
    public List<BookItem> search(Collection<Library> branches, SearchCriteria criteria) {
        return branches.stream()
                .flatMap(lib -> lib.getAllItems().stream())
                .filter(item -> criteria.matches(item.getBook()))
                .collect(Collectors.toList());
    }

    /**
     * Search and return only available copies.
     */
    public List<BookItem> searchAvailable(Collection<Library> branches, SearchCriteria criteria) {
        return search(branches, criteria).stream()
                .filter(item -> item.getStatus() == BookStatus.AVAILABLE)
                .collect(Collectors.toList());
    }
}