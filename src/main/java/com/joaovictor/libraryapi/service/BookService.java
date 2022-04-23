package com.joaovictor.libraryapi.service;

import com.joaovictor.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);
}
