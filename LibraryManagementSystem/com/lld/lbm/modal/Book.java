package com.lld.lbm.modal;

import java.util.Objects;

public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final int publicationYear;
 
    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn            = Objects.requireNonNull(isbn,            "ISBN cannot be null");
        this.title           = Objects.requireNonNull(title,           "Title cannot be null");
        this.author          = Objects.requireNonNull(author,          "Author cannot be null");
        this.publicationYear = publicationYear;
    }
 
    public String getIsbn()           { return isbn; }
    public String getTitle()          { return title; }
    public String getAuthor()         { return author; }
    public int    getPublicationYear(){ return publicationYear; }

}
