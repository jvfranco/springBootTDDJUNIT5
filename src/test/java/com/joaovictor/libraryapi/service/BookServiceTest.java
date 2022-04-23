package com.joaovictor.libraryapi.service;

import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.model.repository.BookRepository;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.service.impl.BookServiceImpl;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.assertj.core.api.Assert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por Id.")
    public void getByIdTest() {
        Long id = 1L;

        Book book = createNewBook();
        book.setId(id);
        Mockito.when(this.bookRepository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> bookById = this.bookService.getById(id);

        Assertions.assertThat(bookById.isPresent()).isTrue();
        Assertions.assertThat(bookById.get().getId()).isEqualTo(id);
        Assertions.assertThat(bookById.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(bookById.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(bookById.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id quando ele não existe na base.")
    public void bookNotFoundByIdTest() {
        Long id = 1L;
        Mockito.when(this.bookRepository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> bookById = this.bookService.getById(id);

        Assertions.assertThat(bookById.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() {
        Book book = Book.builder().id(1L).build();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow( () -> this.bookService.delete(book) );

        Mockito.verify(this.bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar livro inexistente.")
    public void deleteInvalidBookTest() {
        Book book = Book.builder().build();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->this.bookService.delete(book) );

        Mockito.verify(this.bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        Long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createNewBook();
        updatedBook.setId(id);

        Mockito.when(this.bookRepository.save(updatingBook)).thenReturn(updatedBook);

        Book book = this.bookService.update(updatingBook);

        Assertions.assertThat(book.getId()).isEqualTo(updatedBook.getId());
        Assertions.assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        Assertions.assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        Assertions.assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar livro inexistente.")
    public void updateInvalidBookTest() {
        Book book = Book.builder().build();

        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () ->this.bookService.update(book) );

        Mockito.verify(this.bookRepository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades.")
    public void findBookTest() {
        Book book = createNewBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = List.of(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        Mockito.when(this.bookRepository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class))).thenReturn(page);

        Page<Book> result = this.bookService.find(book, pageRequest);

        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(lista);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    private Book createNewBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
}
