package com.lld.lbm.search;

import com.lld.lbm.modal.Book;

/**
 * Strategy interface for book search criteria.
 *
 * New search types (e.g., by Genre, by Publisher) are added by
 * implementing this interface — no changes to the search engine.
 * Satisfies OCP for the search subsystem.
 */
@FunctionalInterface
public interface SearchCriteria {
    boolean matches(Book book);
}