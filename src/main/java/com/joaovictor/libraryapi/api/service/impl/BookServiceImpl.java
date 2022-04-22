package com.joaovictor.libraryapi.api.service.impl;

import com.joaovictor.libraryapi.api.model.entity.Book;
import com.joaovictor.libraryapi.api.model.repository.BookRepository;
import com.joaovictor.libraryapi.api.service.BookService;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
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
