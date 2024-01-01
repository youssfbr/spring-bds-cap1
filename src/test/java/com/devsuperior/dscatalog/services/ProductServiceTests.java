package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

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
        nonExistingId = 2L;
        dependentId = 3L;
        Product product = Factory.createProduct();
        PageImpl<Product> page = new PageImpl<>(List.of(product));

        when(repository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());

        when(repository.save(ArgumentMatchers.any())).thenReturn(product);

        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
    }

    @Test
    void findAllPagedShouldReturnPage() {

        final PageRequest pageable = PageRequest.of(0 , 10);

        final Page<ProductDTO> result = service.findAllPaged(pageable);

        Assertions.assertNotNull(result);

        verify(repository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    void findByIdShouldReturnProductDTOWhenIdExists() {

        final ProductDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);

        verify(repository).findById(existingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.findById(nonExistingId));

        verify(repository).findById(nonExistingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> service.delete(existingId));

        verify(repository).deleteById(existingId);
        verify(repository, times(1)).deleteById(existingId);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));

        verify(repository, times(1)).deleteById(nonExistingId);
    }

    @Test
    void deleteShouldThrowDatabaseExceptionWhenDependentIdExist() {

        Assertions.assertThrows(DatabaseException.class, () -> service.delete(dependentId));

        verify(repository, times(1)).deleteById(dependentId);
    }

}
