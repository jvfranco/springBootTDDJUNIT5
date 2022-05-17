package com.joaovictor.libraryapi.api.resource;

import com.joaovictor.libraryapi.api.dto.BookDTO;
import com.joaovictor.libraryapi.api.dto.LoanDTO;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.entity.Loan;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book API")
public class BookController {

    private final BookService bookService;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "CREATE A BOOK")
    @ApiResponses(
            @ApiResponse(responseCode = "201", description = "Book created")
    )
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
    @Operation(summary = "OBTAINS A BOOK DETAILS BY ID")
    public BookDTO get(@PathVariable Long id) {
        return this.bookService
                .getById(id)
                .map(book -> this.modelMapper.map(book, BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = this.bookService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        this.bookService.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return this.bookService.getById(id)
                .map(book -> {
                    book.setTitle(bookDTO.getTitle());
                    book.setAuthor(bookDTO.getAuthor());
                    this.bookService.update(book);
                    return modelMapper.map(book, BookDTO.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageRequest) {
        Book filter = modelMapper.map(bookDTO, Book.class);
        Page<Book> result = this.bookService.find(filter, pageRequest);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException((HttpStatus.NOT_FOUND)));
        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanDTO> list = result
                .getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO loanBookDTO = modelMapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(loan, LoanDTO.class);
                    loanDTO.setBook(loanBookDTO);
                    return loanDTO;
                })
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, result.getTotalElements());
    }

}
