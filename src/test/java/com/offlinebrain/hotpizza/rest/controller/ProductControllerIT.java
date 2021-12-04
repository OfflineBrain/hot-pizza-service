package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.AbstractIT;
import com.offlinebrain.hotpizza.data.model.AmountUnit;
import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import com.offlinebrain.hotpizza.data.repository.ProductRepository;
import com.offlinebrain.hotpizza.rest.model.product.CreateProductDTO;
import com.offlinebrain.hotpizza.rest.model.product.ProductDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProductController Integration Test")
class ProductControllerIT extends AbstractIT {
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryControllerIT.class);

    @Autowired
    private ProductController controller;

    @Autowired
    private ProductCategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Required beans loaded")
    void contextLoads() {
        Assertions.assertNotNull(controller);
        Assertions.assertNotNull(categoryRepository);
        Assertions.assertNotNull(productRepository);
        Assertions.assertNotNull(restTemplate);
    }


    @Nested
    @DisplayName("Product creation API")
    class ProductCreation {
        private String path = "/products";
        private ProductCategory productCategory;

        private String productName = "ProductControllerIT.productName";

        @BeforeEach
        void setUp() {
            productCategory = categoryRepository.saveAndFlush(ProductCategory.builder()
                    .name("ProductControllerIT.productCategory").build());
        }

        @AfterEach
        void tearDown() {
            productRepository.deleteAll();
            categoryRepository.deleteAll();
        }

        @Test
        @DisplayName("Create product")
        void createProduct() {
            CreateProductDTO createProductDTO = CreateProductDTO.builder()
                    .name(productName)
                    .price(new BigDecimal("99.99"))
                    .description("Awesome description")
                    .amountUnit(AmountUnit.g)
                    .categoryUUID(productCategory.getUuid())
                    .amount(1000)
                    .build();

            ResponseEntity<EntityModel<ProductDTO>> response = restTemplate.exchange(path, HttpMethod.POST,
                    new HttpEntity<>(createProductDTO),
                    new ParameterizedTypeReference<>() {
                    });

            ProductDTO productDTO = assertProductCategoryCreation(response, productName);
            logger.info(productDTO::toString);
        }

        private ProductDTO assertProductCategoryCreation(ResponseEntity<EntityModel<ProductDTO>> response,
                                                         String name) {
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            ProductDTO product = response.getBody().getContent();
            assertNotNull(product);
            assertEquals(name, product.getName());
            assertNotNull(product.getUuid());
            return product;
        }
    }

    @Nested
    @DisplayName("Product retrieve API")
    class ProductRetrieval {
        private final ArrayList<String> firstProductNames = Lists.newArrayList(
                "ProductCategoryControllerIT.categoryName1",
                "ProductCategoryControllerIT.categoryName2");
        private final ArrayList<String> secondProductNames = Lists.newArrayList(
                "ProductCategoryControllerIT.categoryName11",
                "ProductCategoryControllerIT.categoryName12",
                "ProductCategoryControllerIT.categoryName13");
        private final String path = "/products";
        private ProductCategory firstProductCategory;
        private ProductCategory secondProductCategory;
        private List<Product> dbProducts = new ArrayList<>();

        @BeforeEach
        void setUp() {
            firstProductCategory = categoryRepository.saveAndFlush(ProductCategory.builder()
                    .name("ProductControllerIT.productCategory1").build());
            secondProductCategory = categoryRepository.saveAndFlush(ProductCategory.builder()
                    .name("ProductControllerIT.productCategory2").build());

            dbProducts.addAll(createProducts(firstProductNames, firstProductCategory));
            dbProducts.addAll(createProducts(secondProductNames, secondProductCategory));
        }

        @AfterEach
        void tearDown() {
            productRepository.deleteAll();
            categoryRepository.deleteAll();
        }

        @Test
        @DisplayName("Get product by name")
        void testGetByName() {
            String name = firstProductNames.get(0);
            String getPath = path + "/" + name;

            ResponseEntity<EntityModel<ProductDTO>> response = restTemplate.exchange(getPath, HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            ProductDTO product = response.getBody().getContent();
            assertNotNull(product);
            assertEquals(name, product.getName());
            assertNotNull(product.getUuid());
        }

        @Test
        @DisplayName("Get all products")
        void testGetAll() {
            ResponseEntity<CollectionModel<EntityModel<ProductDTO>>> response = restTemplate.exchange(path,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Collection<EntityModel<ProductDTO>> collection = response.getBody().getContent();
            assertFalse(collection.isEmpty());
            List<ProductDTO> categories = collection.stream().map(EntityModel::getContent).toList();
            assertEquals(dbProducts.size(), categories.size());
        }

        @Test
        @DisplayName("Get all products by category")
        void testGetByCategory() {
            String getPath = "/categories/" + secondProductCategory.getName() + "/products";
            ResponseEntity<CollectionModel<EntityModel<ProductDTO>>> response = restTemplate.exchange(getPath,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Collection<EntityModel<ProductDTO>> collection = response.getBody().getContent();
            assertFalse(collection.isEmpty());
            List<ProductDTO> categories = collection.stream().map(EntityModel::getContent).toList();
            assertEquals(secondProductNames.size(), categories.size());
        }

        private List<Product> createProducts(ArrayList<String> names, ProductCategory category) {
            return productRepository.saveAllAndFlush(names.stream()
                    .map(name -> Product.builder()
                            .name(name)
                            .price(new BigDecimal("99.99"))
                            .description("Awesome description")
                            .amountUnit(AmountUnit.g)
                            .category(category)
                            .amount(1000)
                            .build()).toList());
        }
    }

    @Nested
    @DisplayName("Product delete API")
    class ProductDeletion {
        private String path = "/products";
        private ProductCategory productCategory;

        private String productName = "ProductControllerIT.productName";

        @BeforeEach
        void setUp() {
            productCategory = categoryRepository.saveAndFlush(ProductCategory.builder()
                    .name("ProductControllerIT.productCategory").build());
        }

        @AfterEach
        void tearDown() {
            productRepository.deleteAll();
            categoryRepository.deleteAll();
        }

        @Test
        @DisplayName("Delete product by name")
        void testProductByName() {
            Product product = Product.builder()
                    .name(productName)
                    .price(new BigDecimal("99.99"))
                    .description("Awesome description")
                    .amountUnit(AmountUnit.g)
                    .category(productCategory)
                    .amount(1000)
                    .build();
            productRepository.saveAndFlush(product);
            String deletePath = path + "/" + productName;

            ResponseEntity<Object> response = restTemplate.exchange(deletePath, HttpMethod.DELETE,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(productRepository.findByName(productName).isEmpty());
        }
    }

}