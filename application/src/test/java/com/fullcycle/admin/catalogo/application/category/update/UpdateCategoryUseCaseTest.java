package com.fullcycle.admin.catalogo.application.category.update;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import java.util.Objects;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        .thenReturn(Optional.of(aCategory));
    when(categoryGateway.update(any())).thenAnswer(returnsFirstArg());

    final var actualOutput = useCase.execute(aCommand).get();

    assertNotNull(actualOutput);
    assertNotNull(actualOutput.id());

    Mockito.verify(categoryGateway, times(1)).findById(eq(expectedId));

    Mockito.verify(categoryGateway, times(1)).update(argThat(
        anUpdatedCategory -> Objects.equals(expectedName, anUpdatedCategory.getName())
            && Objects.equals(expectedDescription, anUpdatedCategory.getDescription())
            && Objects.equals(expectedIsActive, anUpdatedCategory.isActive())
            && Objects.equals(expectedId, anUpdatedCategory.getId())
            && Objects.equals(aCategory.getCreatedAt(), anUpdatedCategory.getCreatedAt())
            && Objects.equals(aCategory.getUpdatedAt(), anUpdatedCategory.getUpdatedAt())
            && Objects.isNull(anUpdatedCategory.getDeletedAt())
    ));

  }
}
