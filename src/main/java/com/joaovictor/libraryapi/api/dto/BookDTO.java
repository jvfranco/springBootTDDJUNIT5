package com.joaovictor.libraryapi.api.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

    private Long id;

    @NotEmpty(message = "Title não pode estar vazio.")
    private String title;

    @NotEmpty(message = "Author não pode estar vazio.")
    private String author;

    @NotEmpty(message = "ISBN não pode estar vazio.")
    private String isbn;
}
