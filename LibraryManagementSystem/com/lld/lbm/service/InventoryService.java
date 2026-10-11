package com.lld.lbm.service;

import java.util.List;
import java.util.stream.Collectors;

import com.lld.lbm.modal.Book;
import com.lld.lbm.modal.BookItem;
import com.lld.lbm.modal.Library;
import com.lld.lbm.repository.LibraryRepository;

/**
 * Manages library branch operations and book inventory.
 * Provides a facade over the LibraryRepository for inventory management.
 */
public class InventoryService {

	private final LibraryRepository libraryRepo;

	public InventoryService(LibraryRepository libraryRepo) {
		this.libraryRepo = libraryRepo;
	}

	/**
	 * Add a new library branch.
	 *
	 * @param branchId unique identifier for the branch
	 * @param name     branch name
	 * @param address  branch address
	 */
	public void addBranch(String branchId, String name, String address) {
		Library branch = new Library(branchId, name, address);
		libraryRepo.save(branch);
		System.out.println("[INVENTORY] Added branch: " + branch);
	}

	/**
	 * Add a physical copy (BookItem) of a book to a specific branch.
	 *
	 * @param itemId   unique identifier for this copy
	 * @param book     the book metadata
	 * @param branchId the branch where this copy will be stored
	 * @throws IllegalArgumentException if the branch does not exist
	 */
	public void addBookItem(String itemId, Book book, String branchId) {
		Library branch = libraryRepo.findById(branchId)
			.orElseThrow(() -> new IllegalArgumentException(
				"Branch not found: " + branchId));

		BookItem item = new BookItem(itemId, book, branchId);
		branch.addBookItem(item);
		System.out.println("[INVENTORY] Added item: " + item);
	}

	/**
	 * Get all registered library branches.
	 *
	 * @return unmodifiable list of all branches
	 */
	public List<Library> getAllBranches() {
		return libraryRepo.findAll().stream()
			.collect(Collectors.toList());
	}

}
