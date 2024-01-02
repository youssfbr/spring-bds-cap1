package com.devsuperior.dscatalog.services;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductServiceIT {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setup() throws Exception {

        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findAllPagedShouldReturnPageWhenPage0Size10() {

        final PageRequest pageRequest = PageRequest.of(0 , 10);

        final Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(25, result.getTotalElements());
    }

    @Test
    void findAllPagedShouldReturnSortedPageWhenSortByName() {

        final PageRequest pageRequest = PageRequest.of(0 , 10, Sort.by("name"));

        final Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

    @Test
    void findAllPagedShouldReturnEmptyWhenPageDoesNotExist() {

        final PageRequest pageRequest = PageRequest.of(50 , 10);

        final Page<ProductDTO> result = service.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void deleteShouldDeleteResourceWhenIdExists() {

        service.delete(existingId);

        Assertions.assertEquals(countTotalProducts - 1, repository.count());
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.delete(nonExistingId));
    }


}
