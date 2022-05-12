package com.joaovictor.libraryapi.api.resource;

import com.joaovictor.libraryapi.api.dto.BookDTO;
import com.joaovictor.libraryapi.api.dto.LoanDTO;
import com.joaovictor.libraryapi.api.dto.LoanFilterDTO;
import com.joaovictor.libraryapi.api.dto.ReturnedLoanDTO;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.entity.Loan;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @Autowired
    public LoanController(LoanService loanService, BookService bookService, ModelMapper modelMapper) {
        this.loanService = loanService;
        this.bookService = bookService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO loanDTO) {
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(() -> new ResponseStatusException( HttpStatus.BAD_REQUEST, "Book não encontrado para o isbn informado." ));
        Loan entity = Loan.builder().book(book).customer(loanDTO.getCustumer()).loanDate(LocalDate.now()).build();

        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(dto.isReturned());
        loanService.update(loan);

    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> loans = result.getContent().stream().map(entity -> {
            Book book = entity.getBook();
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
            loanDTO.setBook(bookDTO);
            return loanDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(loans, pageRequest, result.getTotalElements());
    }


}
