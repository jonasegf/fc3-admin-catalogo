package com.fullcycle.admin.catalogo.application.category.delete;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class DeleteCategoryUseCaseTest {

  @InjectMocks
  private DefaultDeleteCategoryUseCase useCase;

  @Mock
  private CategoryGateway categoryGateway;

  @BeforeEach
  void cleanup() {
    reset(categoryGateway);
  }

  @Test
  public void givenAValidId_whenCallsDeleteCategory_shouldBeOk() {
    final var aCategory = Category.newCategory("Filmes", "A categoria mais top", true);
    final var expectedId = aCategory.getId();

    doNothing().when(categoryGateway).deleteById(eq(expectedId));

    Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

    Mockito.verifyNoInteractions(categoryGateway, times(1).deleteById(eq(expectedId)));

  }

  @Test
  public void givenAInvalidId_whenCallsDeleteCategory_shouldBeOk() {
    final var expectedId = CategoryId.from("123");

    doNothing().when(categoryGateway).deleteById(eq(expectedId));

    Assertions.assertDoesNotThrow(() -> useCase.execute(expectedId.getValue()));

    Mockito.verifyNoInteractions(categoryGateway, times(1).deleteById(eq(expectedId)));

  }

  @Test
  public void givenAValidId_whenGatewayThrowsError_shouldReturnException() {
    final var aCategory = Category.newCategory("Filmes", "A categoria mais top", true);
    final var expectedId = aCategory.getId();

    doThrow(new IllegalStateException("Gateway error")).when(categoryGateway)
        .deleteById(eq(expectedId));

    Assertions.assertThrows(IllegalStateException.class,
        () -> useCase.execute(expectedId.getValue()));

    Mockito.verifyNoInteractions(categoryGateway, times(1).deleteById(eq(expectedId)));
  }

}
