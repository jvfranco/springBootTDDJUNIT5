package com.joaovictor.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joaovictor.libraryapi.api.dto.BookDTO;
import com.joaovictor.libraryapi.model.entity.Book;
import com.joaovictor.libraryapi.service.BookService;
import com.joaovictor.libraryapi.exception.BusinessException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")     // Roda os testes somente no contexto de teste
@WebMvcTest                 // testa somente o comportamento da api
@AutoConfigureMockMvc       // Faz uma configuração para controle da injeção de dependencias
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc; //Objeto que irá mockar as requisições

    @MockBean // mock especializado, utilizado pelo spring para instanciar o objeto
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createBookTest() throws Exception {
        BookDTO bookDTO = createNewBookDTO();
        Book savedBook = Book.builder().id(10L).title("As Aventuras").author("Artur").isbn("001").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);

        String json = new ObjectMapper().writeValueAsString(bookDTO);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDTO.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDTO.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDTO.getIsbn()))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect( MockMvcResultMatchers.status().isBadRequest() )
                .andExpect( MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    public void createBookWithDuplicatedIsbn() throws Exception {
        String json = new ObjectMapper().writeValueAsString(this.createNewBookDTO());
        String msg = "Isbn já cadastrado.";
        BDDMockito.given(this.service.save(Mockito.any(Book.class))).willThrow(new BusinessException(msg));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(msg));
    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    public void getBookDetailTest() throws Exception {
        //cenario (given)
        Long id = 1L;
        Book book = createNewBook();
        BDDMockito.given(this.service.getById(id)).willReturn(Optional.of(book));

        //execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(createNewBookDTO().getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(createNewBookDTO().getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(createNewBookDTO().getIsbn()));


    }

    @Test
    @DisplayName("Deve retornar um resource not found quando o livro procurado não existir.")
    public void bookNotFoundTest() throws Exception {
        //cenario (given)
        Book book = createNewBook();
        BDDMockito.given(this.service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        //execucao (when)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    private Book createNewBook() {
        return Book.builder().id(1L).title("As Aventuras").author("Artur").isbn("001").build();
    }

    private BookDTO createNewBookDTO() {
        return BookDTO.builder().id(1L).title("As Aventuras").author("Artur").isbn("001").build();
    }
}
