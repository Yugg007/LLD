package com.lld.lbm.repository;


import java.util.Collection;
import java.util.Optional;

/**
 * Generic CRUD contract for all repositories.
 * Keeps storage concerns isolated from service logic (SRP).
 */
public interface Repository<T, ID> {
    void    save(T entity);
    Optional<T> findById(ID id);
    Collection<T> findAll();
    void    delete(ID id);
}