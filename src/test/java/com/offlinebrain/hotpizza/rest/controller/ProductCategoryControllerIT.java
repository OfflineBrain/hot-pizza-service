package com.offlinebrain.hotpizza.rest.controller;

import com.offlinebrain.hotpizza.AbstractIT;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.data.repository.ProductCategoryRepository;
import com.offlinebrain.hotpizza.rest.model.category.CategoryModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.opentest4j.AssertionFailedError;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("ProductCategoryController Integration Test")
class ProductCategoryControllerIT extends AbstractIT {

    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryControllerIT.class);
    private final String url = "/categories";

    @Autowired
    private ProductCategoryController controller;

    @Autowired
    private ProductCategoryRepository repository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Required beans loaded")
    void contextLoads() {
        Assertions.assertNotNull(controller);
        Assertions.assertNotNull(repository);
        Assertions.assertNotNull(restTemplate);
    }

    @Nested
    @DisplayName("ProductCategory creation API")
    class ProductCategoryCreation {
        String testCategoryName = "ProductCategoryControllerIT.categoryName";
        String testParentCategoryName = "ProductCategoryControllerIT.parentCategoryName";
        String testSubcategoryName = "ProductCategoryControllerIT.subcategoryName";

        @AfterEach
        void tearDown() {
            repository.deleteAll();
        }

        @Test
        @DisplayName("Create single product category")
        void testProductCategoryCreation() {

            ResponseEntity<EntityModel<CategoryModel>> response = restTemplate.exchange(url, HttpMethod.POST,
                    new HttpEntity<>(CategoryModel.builder().name(testCategoryName).build()),
                    new ParameterizedTypeReference<>() {
                    });


            CategoryModel categoryModel = assertProductCategoryCreation(response, testCategoryName);
            logger.info(categoryModel::toString);
        }

        @Test
        @DisplayName("Create product subcategory")
        void testProductSubCategoryCreation() {
            ProductCategory parentCategory = repository.saveAndFlush(
                    ProductCategory.builder().name(testParentCategoryName).build());

            assertNotNull(parentCategory, "Parent category must be created");

            ResponseEntity<EntityModel<CategoryModel>> subCategoryResponse = restTemplate.exchange(url,
                    HttpMethod.POST,
                    new HttpEntity<>(
                            CategoryModel.builder().name(testSubcategoryName).parent(parentCategory.getUuid()).build()),
                    new ParameterizedTypeReference<>() {
                    });

            CategoryModel subCategoryModel = assertProductCategoryCreation(subCategoryResponse, testSubcategoryName);
            assertEquals(parentCategory.getUuid(), subCategoryModel.getParent());
            logger.info(subCategoryModel::toString);
        }

        private CategoryModel assertProductCategoryCreation(ResponseEntity<EntityModel<CategoryModel>> response,
                                                            String name) {
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            CategoryModel category = response.getBody().getContent();
            assertNotNull(category);
            assertEquals(name, category.getName());
            assertNotNull(category.getUuid());
            return category;
        }
    }

    @Nested
    @DisplayName("ProductCategory access API")
    class ProductCategoryRetrieval {

        ArrayList<String> testCategoryNames = Lists.newArrayList("ProductCategoryControllerIT.categoryName1",
                "ProductCategoryControllerIT.categoryName2",
                "ProductCategoryControllerIT.categoryName3");
        String testParentCategoryName = "ProductCategoryControllerIT.parentCategoryName";
        ArrayList<String> testSubcategoryNames = Lists.newArrayList("ProductCategoryControllerIT.subcategoryName1",
                "ProductCategoryControllerIT.subcategoryName2");

        List<ProductCategory> dbCategories;

        @BeforeEach
        void setUp() {
            List<ProductCategory> productCategoriesToSave = testCategoryNames.stream()
                    .map(name -> ProductCategory.builder().name(name).build())
                    .toList();
            List<ProductCategory> productCategories = repository.saveAllAndFlush(productCategoriesToSave);
            assertFalse(productCategories.isEmpty(), "Category list must be created");
            ProductCategory parentCategory = repository.saveAndFlush(
                    ProductCategory.builder().name(testParentCategoryName).build());
            assertNotNull(parentCategory, "Parent category must be created");
            productCategories.add(parentCategory);
            List<ProductCategory> productSubCategoriesToSave = testSubcategoryNames.stream()
                    .map(name -> ProductCategory.builder().name(name).parent(parentCategory).build())
                    .toList();
            List<ProductCategory> subCategory = repository.saveAllAndFlush(productSubCategoriesToSave);
            assertFalse(subCategory.isEmpty(), "Sub categories must be created");
            productCategories.addAll(subCategory);

            dbCategories = productCategories;
        }

        @AfterEach
        void tearDown() {
            repository.deleteAll();
        }

        @Test
        @DisplayName("Get all categories")
        void testGetAllCategories() {
            ResponseEntity<CollectionModel<EntityModel<CategoryModel>>> response = restTemplate.exchange(url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY, new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Collection<EntityModel<CategoryModel>> collection = response.getBody().getContent();
            assertFalse(collection.isEmpty());
            List<CategoryModel> categories = collection.stream().map(EntityModel::getContent).toList();
            assertEquals(dbCategories.size(), categories.size());
            logger.info(() -> categories.stream()
                    .map(CategoryModel::toString)
                    .collect(Collectors.joining(System.lineSeparator(), "Retrieved categories" + System.lineSeparator(),
                            "")));
        }

        @Test
        @DisplayName("Get all subcategories by category name")
        void testGetAllSubCategoriesByName() {
            ProductCategory parentCategory = repository.findByName(testParentCategoryName)
                    .orElseThrow(() -> new AssertionFailedError("Parent category not found"));

            String path = url + "/" + testParentCategoryName + "/subcategories";
            ResponseEntity<CollectionModel<CategoryModel>> response = restTemplate.exchange(
                    path,
                    HttpMethod.GET,
                    HttpEntity.EMPTY, new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Collection<CategoryModel> categories = response.getBody().getContent();
            assertFalse(categories.isEmpty());
            assertEquals(testSubcategoryNames.size(), categories.size());
            assertTrue(categories.stream()
                    .allMatch(categoryModel -> categoryModel.getParent().equals(parentCategory.getUuid())));
            logger.info(() -> categories.stream()
                    .map(CategoryModel::toString)
                    .collect(Collectors.joining(System.lineSeparator(), "Retrieved categories" + System.lineSeparator(),
                            "")));
        }

        @Test
        @DisplayName("Get category by name")
        void testGetCategoryByName() {
            String path = url + "/" + testParentCategoryName;
            ResponseEntity<EntityModel<CategoryModel>> response = restTemplate.exchange(
                    path,
                    HttpMethod.GET,
                    HttpEntity.EMPTY, new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            CategoryModel category = response.getBody().getContent();
            assertNotNull(category);
            assertEquals(testParentCategoryName, category.getName());
            assertNotNull(category.getUuid());
        }
    }

    @Nested
    @DisplayName("ProductCategory delete API")
    class ProductCategoryDeletion {
        String testCategoryName = "ProductCategoryControllerIT.categoryToDelete";

        @BeforeEach
        void setUp() {
            repository.saveAndFlush(ProductCategory.builder().name(testCategoryName).build());
        }

        @AfterEach
        void tearDown() {
            repository.deleteAll();
        }

        @Test
        @DisplayName("Delete category by name")
        void testDeleteCategoryByName() {
            String path = url + "/" + testCategoryName;
            ResponseEntity<Object> response = restTemplate.exchange(path, HttpMethod.DELETE, HttpEntity.EMPTY,
                    new ParameterizedTypeReference<>() {
                    });

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertTrue(repository.findByName(testCategoryName).isEmpty());
        }
    }
}