package com.joaovictor.libraryapi.service;

import com.joaovictor.libraryapi.exception.BusinessException;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.entity.Loan;
import com.joaovictor.libraryapi.model.repository.LoanRepository;
import com.joaovictor.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class LoanServiceTest {

    @MockBean
    private LoanRepository repository;
    private LoanService loanService;

    @BeforeEach
    public void setUp() {
        this.loanService = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo.")
    public void saveLoanTest() {
        Book book = Book.builder().id(1L).build();
        Loan loanSaving = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();

        Loan loanSaved = Loan.builder().id(1L).book(book).customer("Fulano").loanDate(LocalDate.now()).build();

        Mockito.when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        Mockito.when(repository.save(loanSaving)).thenReturn(loanSaved);

        Loan loan = loanService.save(loanSaving);

        Assertions.assertThat(loan.getId()).isEqualTo(loanSaved.getId());
        Assertions.assertThat(loan.getBook().getId()).isEqualTo(book.getId());
        Assertions.assertThat(loan.getCustomer()).isEqualTo(loanSaved.getCustomer());
        Assertions.assertThat(loan.getLoanDate()).isEqualTo(loanSaved.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar um erro de negócio ao salvar um empréstimo com livro já emprestado.")
    public void loanedBookTest() {
        Loan loanSaving = this.createLoan();

        Mockito.when(repository.existsByBookAndNotReturned(loanSaving.getBook())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> loanService.save(loanSaving));

        Assertions.assertThat(exception).isInstanceOf(BusinessException.class)
                .hasMessage("Book já emprestado.");

        Mockito.verify(repository, Mockito.never()).save(loanSaving);
    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo id.")
    public void getLoanDetailsTest() {
        //cenario
        Long id = 1L;
        Loan loan = this.createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //execucao
        Optional<Loan> result = loanService.getById(id);

        //verificacao
        Assertions.assertThat(result.isPresent()).isTrue();
        Assertions.assertThat(result.get().getId()).isEqualTo(id);
        Assertions.assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        Assertions.assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        Assertions.assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo.")
    public void updateLoanTest() {
        Long id = 1L;
        Loan loan = this.createLoan();
        loan.setId(id);
        loan.setReturned(true);

        Mockito.when( repository.save(loan) ).thenReturn(loan);

        Loan updateLoan = loanService.update(loan);

        Assertions.assertThat(updateLoan.getReturned()).isTrue();
        Mockito.verify(repository).save(loan);
    }

    private Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        return Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
    }
}