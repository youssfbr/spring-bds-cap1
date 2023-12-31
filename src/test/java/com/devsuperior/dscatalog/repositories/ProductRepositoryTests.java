package com.devsuperior.dscatalog.repositories;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@DataJpaTest
class ProductRepositoryTests {

    @Autowired
    private ProductRepository repository;
    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setup() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findByIdShouldReturnPresentOptionalWhenIdExists() {

        final Optional<Product> result = repository.findById(existingId);

        Assertions.assertTrue(result.isPresent());
    }

    @Test
    void findByIdThrowEntityNotFoundExceptionWhenIdDoesNotExist() {

        final Optional<Product> result = repository.findById(nonExistingId);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        final Product productCreated = repository.save(product);

        Assertions.assertNotNull(productCreated.getId());
        Assertions.assertEquals(countTotalProducts + 1, productCreated.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        final Optional<Product> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }
}
