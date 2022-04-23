package com.joaovictor.libraryapi.service;

import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.repository.BookRepository;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.service.impl.BookServiceImpl;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setUp() {
        this.bookService = new BookServiceImpl( bookRepository );
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    public void saveBookTest() {
        //cenario
        Book book = createNewBook();
        Mockito.when(this.bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);

        Mockito.when(bookRepository.save(book))
                .thenReturn(Book.builder()
                    .id(1l)
                    .isbn("123")
                    .author("Fulano")
                    .title("As aventuras")
                    .build()
                );

        //execucao
        Book savedBook = this.bookService.save(book);

        //verificacao
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado.")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        //cenario
        Book book = this.createNewBook();
        Mockito.when(this.bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable exception = Assertions.catchThrowable(() -> this.bookService.save(book));

        //verificao
        Assertions.assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        //verifica que o repository nunca executou o metodo com determinado parametro
        Mockito.verify(this.bookRepository, Mockito.never()).save(book);
    }

    private Book createNewBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
}
