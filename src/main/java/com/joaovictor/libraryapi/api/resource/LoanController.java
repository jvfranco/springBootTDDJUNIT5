package com.joaovictor.libraryapi.api.resource;

import com.joaovictor.libraryapi.api.dto.LoanDTO;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.entity.Loan;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(() -> new ResponseStatusException( HttpStatus.BAD_REQUEST, "Book não encontrado para o isbn informado." ));
        Loan entity = Loan.builder().book(book).customer(loanDTO.getCustumer()).loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);
        return entity.getId();
    }

}
