package com.fullcycle.admin.catalogo.application.category.retrieve.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

@IntegrationTest
public class GetCategoryByIdUseCaseIT {

  @Autowired
  private GetCategoryByIdUseCase useCase;
  @Autowired
  private CategoryRepository categoryRepository;
  @SpyBean
  private CategoryGateway categoryGateway;

  @Test
  public void givenAValidId_whenCallsGetCategory_shouldReturnCategory() {
    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;

    final var aCategory = Category.newCategory(expectedName, expectedDescription, expectedIsActive);
    final var expectedId = aCategory.getId();

    save(aCategory);

    final var actualCategory = useCase.execute(expectedId.getValue());

    assertEquals(expectedId, actualCategory.id());
    assertEquals(expectedName, actualCategory.name());
    assertEquals(expectedDescription, actualCategory.description());
    assertEquals(expectedIsActive, actualCategory.isActive());
    assertEquals(aCategory.getCreatedAt(), actualCategory.createdAt());
    assertEquals(aCategory.getUpdatedAt(), actualCategory.updatedAt());
    assertEquals(aCategory.getDeletedAt(), actualCategory.deletedAt());
  }

  @Test
  public void givenAnInvalidId_whenCallsGetCategory_shouldReturnNotFound() {
    final var expectedErrorMessage = "Category with ID 123 was not found";
    final var expectedId = CategoryId.from("123");

    final var actuaException = assertThrows(DomainException.class,
        () -> useCase.execute(expectedId.getValue()));

    assertEquals(expectedErrorMessage, actuaException.getMessage());
  }

  @Test
  public void givenAValidId_whenGatewayThrowsError_shouldReturnException() {
    final var expectedErrorMessage = "Gateway Error";
    final var expectedId = CategoryId.from("123");

    doThrow(new IllegalStateException("Gateway Error"))
        .when(categoryGateway).findById(eq(expectedId));

    final var actuaException = assertThrows(IllegalStateException.class,
        () -> useCase.execute(expectedId.getValue()));

    assertEquals(expectedErrorMessage, actuaException.getMessage());
  }

  private void save(final Category... aCategory) {
    categoryRepository.saveAllAndFlush(
        Arrays.stream(aCategory)
            .map(CategoryJpaEntity::from)
            .toList());
  }
}
