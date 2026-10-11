package com.book.show.repository;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
	T save(T entity);
	List<T> findAll();
	void delete(Long id);
	Optional<T> findById(Long id);

}