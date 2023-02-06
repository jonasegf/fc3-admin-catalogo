package com.fullcycle.admin.catalogo.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.infrastructure.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
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

    assertEquals(0,categoryRepository.count());

    final var actualCategory = categoryGateway.create(aCategory);

    assertEquals(1,categoryRepository.count());

    assertEquals(aCategory.getId(),actualCategory.getId());
    assertEquals(expectedName,actualCategory.getName());
    assertEquals(expectedDescription,actualCategory.getDescription());
    assertEquals(expectedIsActive,actualCategory.isActive());
    assertEquals(aCategory.getCreatedAt(),actualCategory.getCreatedAt());
    assertEquals(aCategory.getUpdatedAt(),actualCategory.getUpdatedAt());
    assertEquals(aCategory.getDeletedAt(),actualCategory.getDeletedAt());
    assertNull(actualCategory.getDeletedAt());

    final var actualEntity = categoryRepository.findById(aCategory.getId().getValue()).get();

    assertEquals(aCategory.getId().getValue(),actualEntity.getId());
    assertEquals(expectedName,actualEntity.getName());
    assertEquals(expectedDescription,actualEntity.getDescription());
    assertEquals(expectedIsActive,actualEntity.isActive());
    assertEquals(aCategory.getCreatedAt(),actualEntity.getCreatedAt());
    assertEquals(aCategory.getUpdatedAt(),actualEntity.getUpdatedAt());
    assertEquals(aCategory.getDeletedAt(),actualEntity.getDeletedAt());
    assertNull(actualEntity.getDeletedAt());
  }

}
