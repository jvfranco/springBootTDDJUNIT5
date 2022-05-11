package com.joaovictor.libraryapi.model.repository;

import com.joaovictor.libraryapi.model.entity.Book;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest //utiliza o banco H2 para realização dos testes
public class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado.")
    public void returnTrueWhenIsbnExists() {
        //cenario
        String isbn = "123";
        Book book = createNewBook();
        testEntityManager.persist(book);

        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado.")
    public void returnFalseWhenIsbnNotExists() {
        //cenario
        String isbn = "123";

        //execucao
        boolean exists = bookRepository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Deve obter um livro por Id.")
    public void findByIdTest() {
        Book book = createNewBook();
        testEntityManager.persist(book);

        Optional<Book> optionalBook = this.bookRepository.findById(book.getId());

        Assertions.assertThat(optionalBook.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        Book book = createNewBook();

        Book savedBook = this.bookRepository.save(book);

        Assertions.assertThat(savedBook.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        Book book = createNewBook();
        testEntityManager.persist(book);

        Book bookFounded = testEntityManager.find(Book.class, book.getId());

        this.bookRepository.delete(bookFounded);

        Book deletedFounded = testEntityManager.find(Book.class, book.getId());

        Assertions.assertThat(deletedFounded).isNull();
    }

    public static Book createNewBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
}
