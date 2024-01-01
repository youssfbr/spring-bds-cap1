package com.devsuperior.dscatalog.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4;

        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingId);
        });

        verify(repository).deleteById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
                service.delete(nonExistingId);
                });

        verify(repository, times(1)).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentIdExist() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentId);
        });

        verify(repository, times(1)).deleteById(dependentId);
    }

}
