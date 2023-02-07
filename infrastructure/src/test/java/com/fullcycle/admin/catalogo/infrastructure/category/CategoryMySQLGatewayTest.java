package com.fullcycle.admin.catalogo.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.infrastructure.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@MySQLGatewayTest
public class CategoryMySQLGatewayTest {

  @Autowired
  private CategoryMysqlGateway categoryGateway;

  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void givenAValidCategory_whenCallsCreate_shouldReturnANewCategory() {
    final var expectedName = "Filmes";
    final var expectedDescription = "Categoria mais assistida";
    final var expectedIsActive = true;

    final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

    assertEquals(0, categoryRepository.count());

    final var actualCategory = categoryGateway.create(aCategory);

    assertEquals(1, categoryRepository.count());

    assertEquals(aCategory.getId(), actualCategory.getId());
    assertEquals(expectedName, actualCategory.getName());
    assertEquals(expectedDescription, actualCategory.getDescription());
    assertEquals(expectedIsActive, actualCategory.isActive());
    assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
    assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
    assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    assertNull(actualCategory.getDeletedAt());

    final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

    assertEquals(aCategory.getId().getValue(), actualEntity.getId());
    assertEquals(expectedName, actualEntity.getName());
    assertEquals(expectedDescription, actualEntity.getDescription());
    assertEquals(expectedIsActive, actualEntity.isActive());
    assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
    assertEquals(aCategory.getUpdatedAt(), actualEntity.getUpdatedAt());
    assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
    assertNull(actualEntity.getDeletedAt());
  }

  @Test
  void givenAValidCategory_whenCallsUpdate_shouldReturnCategoryUpdated() {
    final var expectedName = "Filmes";
    final var expectedDescription = "Categoria mais assistida";
    final var expectedIsActive = true;

    final var aCategory = Category.newCategory("film", null, expectedIsActive);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

    assertEquals(1, categoryRepository.count());

    final var actualInvalidEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

    assertEquals("film", actualInvalidEntity.getName());
    assertNull(actualInvalidEntity.getDescription());
    assertEquals(expectedIsActive, actualInvalidEntity.isActive());

    final var anUpdatedCategory = aCategory.clone()
        .update(expectedName, expectedDescription, expectedIsActive);

    final var actualCategory = categoryGateway.update(anUpdatedCategory);

    assertEquals(1, categoryRepository.count());

    assertEquals(aCategory.getId(), actualCategory.getId());
    assertEquals(expectedName, actualCategory.getName());
    assertEquals(expectedDescription, actualCategory.getDescription());
    assertEquals(expectedIsActive, actualCategory.isActive());
    assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
    assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
    assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    assertNull(actualCategory.getDeletedAt());

    final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

    assertEquals(aCategory.getId().getValue(), actualEntity.getId());
    assertEquals(expectedName, actualEntity.getName());
    assertEquals(expectedDescription, actualEntity.getDescription());
    assertEquals(expectedIsActive, actualEntity.isActive());
    assertEquals(aCategory.getCreatedAt(), actualEntity.getCreatedAt());
    assertTrue(aCategory.getUpdatedAt().isBefore(actualCategory.getUpdatedAt()));
    assertEquals(aCategory.getDeletedAt(), actualEntity.getDeletedAt());
    assertNull(actualEntity.getDeletedAt());
  }

  @Test
  void givenAPrePersistedCategoryAndValidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
    final var aCategory = Category.newCategory("Filmes", "a categoria", true);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

    assertEquals(1, categoryRepository.count());

    categoryGateway.deleteById(aCategory.getId());

    Assertions.assertEquals(0, categoryRepository.count());
  }

  @Test
  void givenInvalidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
    assertEquals(0, categoryRepository.count());

    categoryGateway.deleteById(CategoryId.from("invalid"));

    Assertions.assertEquals(0, categoryRepository.count());
  }

  @Test
  void givenAPrePersistedCategoryAndValidCategoryId_whenCallsFindById_shouldReturnCategory() {
    final var expectedName = "Filmes";
    final var expectedDescription = "Categoria mais assistida";
    final var expectedIsActive = true;

    final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAndFlush(CategoryJpaEntity.from(aCategory));

    assertEquals(1, categoryRepository.count());

    final var actualCategory = categoryGateway.findById(aCategory.getId()).get();

    assertEquals(1, categoryRepository.count());

    assertEquals(aCategory.getId(), actualCategory.getId());
    assertEquals(expectedName, actualCategory.getName());
    assertEquals(expectedDescription, actualCategory.getDescription());
    assertEquals(expectedIsActive, actualCategory.isActive());
    assertEquals(aCategory.getCreatedAt(), actualCategory.getCreatedAt());
    assertEquals(aCategory.getUpdatedAt(), actualCategory.getUpdatedAt());
    assertEquals(aCategory.getDeletedAt(), actualCategory.getDeletedAt());
    assertNull(actualCategory.getDeletedAt());
  }

  @Test
  void givenAValidCategoryIdNotStored_whenCallsFindById_shouldReturnEmpty() {

    assertEquals(0, categoryRepository.count());

    final var actualCategory = categoryGateway.findById(CategoryId.from("empty"));

    assertTrue(actualCategory.isEmpty());
  }

}
