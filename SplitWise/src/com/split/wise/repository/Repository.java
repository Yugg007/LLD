package com.split.wise.repository;

import java.util.Collection;

public interface Repository<T> {
    T save(T entity);
    T findById(Long id);
    Collection<T> findAll();
    void delete(Long id);
}
