package com.joaovictor.libraryapi.service;


import com.joaovictor.libraryapi.api.resource.BookController;
import com.joaovictor.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save( Loan loan );

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
