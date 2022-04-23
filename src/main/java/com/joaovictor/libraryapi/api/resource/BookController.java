package com.joaovictor.libraryapi.api.resource;

import com.joaovictor.libraryapi.api.dto.BookDTO;
import com.joaovictor.libraryapi.api.exception.ApiErrors;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private BookService bookService;
    private ModelMapper modelMapper;

    @Autowired
    public BookController(BookService bookService, ModelMapper modelMapper) {
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO) {
        /*Book book = Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .isbn(bookDTO.getIsbn())
                .build();*/

        Book book = this.modelMapper.map(bookDTO, Book.class);

        book = this.bookService.save(book);

        /*bookDTO = BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .build();*/

        return this.modelMapper.map(book, BookDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO get(@PathVariable Long id) {
        return this.bookService
                .getById(id)
                .map(book -> this.modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException ex) {
        return new ApiErrors(ex);
    }
}
