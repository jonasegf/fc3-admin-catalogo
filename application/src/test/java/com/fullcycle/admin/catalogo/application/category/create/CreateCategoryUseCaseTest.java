package com.fullcycle.admin.catalogo.application.category.create;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import java.util.Objects;
import org.junit.jupiter.api.Test;

public class CreateCategoryUseCaseTest {

  // 1. Teste do caminho feliz
  // 2. Teste passando uma propriedade inválida (name)
  // 3. Teste criando uma categoria inativa
  // 4. Teste simulando um erro generico vindo do gateway

  @Test
  public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
    final var expectedName = "Filmes";
    final var expectedDescription = "A categoria mais assistida";
    final var expectedIsActive = true;

    final var aCommand =
        CreateCategoryCommand.with(expectedName, expectedDescription, expectedIsActive);

    final CategoryGateway categoryGateway = mock(CategoryGateway.class);

    when(categoryGateway.create(any()))
        .thenAnswer(returnsFirstArg());

    final var useCase = new DefaultCreateCategoryUseCase(categoryGateway);

    final var actualOutput = useCase.execute(aCommand);

    assertNotNull(actualOutput);
    assertNotNull(actualOutput.id());

    verify(categoryGateway, times(1)).create(argThat(aCategory ->
        Objects.equals(expectedName, aCategory.getName())
            && Objects.equals(expectedDescription, aCategory.getDescription())
            && Objects.equals(expectedIsActive, aCategory.isActive())
            && Objects.nonNull(aCategory.getId())
            && Objects.nonNull(aCategory.getCreatedAt())
            && Objects.nonNull(aCategory.getUpdatedAt())
            && Objects.isNull(aCategory.getDeletedAt())
    ));
  }
}