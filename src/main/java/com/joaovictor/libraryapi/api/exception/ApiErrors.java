package com.joaovictor.libraryapi.api.exception;

import com.joaovictor.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {
    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> {
            this.errors.add(error.getDefaultMessage());
        });
    }

    public ApiErrors(BusinessException ex) {
        this.errors = List.of(ex.getMessage());
    }

    public ApiErrors(ResponseStatusException ex) {
        this.errors = List.of(ex.getReason());
    }

    public List<String> getErrors() {
        return this.errors;
    }
}
