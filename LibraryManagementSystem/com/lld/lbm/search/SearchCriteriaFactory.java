package com.lld.lbm.search;

import com.lld.lbm.modal.Book;

/**
 * Factory for creating {@link SearchCriteria} implementations.
 * Provides static methods for common search filters and composition.
 *
 * Demonstrates the Strategy pattern: each criteria is a simple predicate,
 * and new search types can be added without modifying existing code.
 */
public class SearchCriteriaFactory {

    private SearchCriteriaFactory() {
        // Utility class; no instantiation
    }

    /**
     * Search by book title (case-insensitive substring match).
     */
    public static SearchCriteria byTitle(String titleFragment) {
        return book -> book.getTitle().toLowerCase()
                .contains(titleFragment.toLowerCase());
    }

    /**
     * Search by book author (case-insensitive substring match).
     */
    public static SearchCriteria byAuthor(String authorFragment) {
        return book -> book.getAuthor().toLowerCase()
                .contains(authorFragment.toLowerCase());
    }

    /**
     * Search by publication year (exact match).
     */
    public static SearchCriteria byPublicationYear(int year) {
        return book -> book.getPublicationYear() == year;
    }

    /**
     * Composite AND: both criteria must match.
     */
    public static SearchCriteria and(SearchCriteria criteria1, SearchCriteria criteria2) {
        return book -> criteria1.matches(book) && criteria2.matches(book);
    }

    /**
     * Composite OR: either criterion must match.
     */
    public static SearchCriteria or(SearchCriteria criteria1, SearchCriteria criteria2) {
        return book -> criteria1.matches(book) || criteria2.matches(book);
    }

    /**
     * NOT: negate a criterion.
     */
    public static SearchCriteria not(SearchCriteria criteria) {
        return book -> !criteria.matches(book);
    }

}
