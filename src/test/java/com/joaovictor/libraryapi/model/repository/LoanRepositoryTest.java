package com.joaovictor.libraryapi.model.repository;


import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.entity.Loan;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

import java.time.LocalDate;

import static com.joaovictor.libraryapi.model.repository.BookRepositoryTest.createNewBook;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe empréstimo não devolvido para o livro.")
    public void existsByBookAndNotReturnedTest() {
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(LocalDate.now()).build();
        entityManager.persist(loan);

        boolean exists = repository.existsByBookAndNotReturned(book);

        Assertions.assertThat(exists).isTrue();

    }
}