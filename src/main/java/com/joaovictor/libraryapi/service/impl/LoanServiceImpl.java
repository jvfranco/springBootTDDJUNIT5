package com.joaovictor.libraryapi.service.impl;

import com.joaovictor.libraryapi.exception.BusinessException;
import com.joaovictor.libraryapi.model.entity.Loan;
import com.joaovictor.libraryapi.model.repository.LoanRepository;
import com.joaovictor.libraryapi.service.LoanService;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {
    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book já emprestado.");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }
}
