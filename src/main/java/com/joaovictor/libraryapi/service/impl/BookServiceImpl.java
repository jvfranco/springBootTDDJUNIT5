package com.joaovictor.libraryapi.service.impl;

import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.repository.BookRepository;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book book) {
        if (this.bookRepository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return this.bookRepository.save(book);
    }

}
