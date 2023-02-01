package com.fullcycle.admin.catalogo.application.category.update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.exceptions.DomainException;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateCategoryUseCaseTest {
  @InjectMocks
  private DefaultUpdateCategoryUseCase useCase;

  @Mock
  private CategoryGateway categoryGateway;

  // 1. Teste do caminho feliz
  // 2. Teste passando uma propriedade inválida (name)
  // 3. Teste criando uma categoria inativa
  // 4. Teste simulando um erro generico vindo do gateway
  // 5. Teste atualizar categoria passando ID invalido

  @Test
  public void givenAValidCommand_whenCallsUpdateCategory_shouldReturnCategoryId() {
    final var aCategory = Category.newCategory("Film", null, true);

    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;

    final var expectedId = aCategory.getId();

    final var aCommand = UpdateCategoryCommand.with(
        expectedId.getValue(),
        expectedName,
        expectedDescription,
        expectedIsActive
    );

    when(categoryGateway.findById(eq(expectedId)))
        .thenReturn(Optional.of(aCategory.clone()));
    when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

    final var actualOutput = useCase.execute(aCommand).get();

    assertNotNull(actualOutput);
    assertNotNull(actualOutput.id());

    verify(categoryGateway, times(1)).findById(eq(expectedId));

    verify(categoryGateway, times(1)).update(argThat(
        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
            && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
            && Objects.equals(expectedId, anUpdatedCategory.getId())
            && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
            && Objects.isNull(anUpdatedCategory.getDeletedAt())
    ));

  }

  @Test
  public void givenAInvalidName_whenCallsUpdateCategory_thenShouldReturnDomainException() {
    final var aCategory = Category.newCategory("Film", null, true);

    final var expectedId = aCategory.getId();
    final String expectedName = null;
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;

    final var expectedErrorMessage = "'name' should not be null";
    final var expectedErrorCount = 1;

    final var aCommand = UpdateCategoryCommand.with(
        expectedId.getValue(),
        expectedName,
        expectedDescription,
        expectedIsActive
    );

    when(categoryGateway.findById(eq(expectedId)))
        .thenReturn(Optional.of(aCategory.clone()));

    final var notification = useCase.execute(aCommand).getLeft();

    assertEquals(expectedErrorCount, notification.getErrors().size());
    assertEquals(expectedErrorMessage, notification.firstError().message());

    verify(categoryGateway, never()).update(any());

  }

  @Test
  public void givenAValidInactivateCommand_whenCallsUpdateCategory_shouldReturnInactiveCategoryId() {
    final var aCategory = Category.newCategory("Film", null, true);

    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = false;

    final var expectedId = aCategory.getId();

    final var aCommand = UpdateCategoryCommand.with(
        expectedId.getValue(),
        expectedName,
        expectedDescription,
        expectedIsActive
    );

    when(categoryGateway.findById(eq(expectedId)))
        .thenReturn(Optional.of(aCategory.clone()));
    when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

    assertTrue(aCategory.isActive());
    assertNull(aCategory.getDeletedAt());

    final var actualOutput = useCase.execute(aCommand).get();

    assertNotNull(actualOutput);
    assertNotNull(actualOutput.id());

    verify(categoryGateway, times(1)).findById(eq(expectedId));

    verify(categoryGateway, times(1)).update(argThat(
        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
            && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
            && Objects.equals(expectedId, anUpdatedCategory.getId())
            && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
            && Objects.nonNull(anUpdatedCategory.getDeletedAt())
    ));

  }

  @Test
  public void givenAValidCommand_whenThrowsRandomException_shouldReturnAnException() {
    final var aCategory = Category.newCategory("Film", null, true);

    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;
    final var expectedId = aCategory.getId();

    final var expectedErrorMessage = "Gateway error";
    final var expectedErrorCount = 1;

    final var aCommand = UpdateCategoryCommand.with(
        expectedId.getValue(),
        expectedName,
        expectedDescription,
        expectedIsActive
    );

    when(categoryGateway.findById(eq(expectedId)))
        .thenReturn(Optional.of(aCategory.clone()));

    when(categoryGateway.update(any()))
        .thenThrow(new IllegalStateException(expectedErrorMessage));

    final var notification = useCase.execute(aCommand).getLeft();

    assertEquals(expectedErrorCount, notification.getErrors().size());
    assertEquals(expectedErrorMessage, notification.firstError().message());

    verify(categoryGateway, times(1)).update(argThat(
        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
            && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
            && Objects.equals(expectedId, anUpdatedCategory.getId())
            && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            && aCategory.getUpdatedAt().isBefore(anUpdatedCategory.getUpdatedAt())
            && Objects.isNull(anUpdatedCategory.getDeletedAt())
    ));

  }

  @Test
  public void givenACommandWithInvalidID_whenCallsUpdateCategory_shouldReturnNotFoundException() {
    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = false;
    final var expectedId = "123";

    final var expectedErrorMessage = "Category with ID 123 was not found";
    final var expectedErrorCount = 1;

    final var aCommand = UpdateCategoryCommand.with(
        expectedId,
        expectedName,
        expectedDescription,
        expectedIsActive
    );

    when(categoryGateway.findById(eq(CategoryId.from(expectedId))))
        .thenReturn(Optional.empty());

    final var actualException =
        assertThrows(DomainException.class, () -> useCase.execute(aCommand).get());

    assertEquals(expectedErrorCount, actualException.getErrors().size());
    assertEquals(expectedErrorMessage, actualException.getErrors().get(0).message());

    verify(categoryGateway, times(1)).findById(eq(CategoryId.from(expectedId)));

    verify(categoryGateway, times(0)).update(any());
  }
}
