package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ListCategoriesUseCaseTest {
  @InjectMocks
  private DefaultListCategoriesUseCase useCase;

  @Mock
  private CategoryGateway categoryGateway;

  @BeforeEach
  void cleanUp() {
    reset(categoryGateway);
  }

  @Test
  void givenaValidQuery_whenCallsListCategories_thenShouldReturnCategories() {
    final var categories = List.of(
        Category.newCategory("Filmes", null, true),
        Category.newCategory("Filmes", null, true)
    );

    final var expectedPage = 0;
    final var expectedPerPage = 10;
    final var terms = "";
    final var expectedSort = "createdAt";
    final var expectedDirection = "asc";

    final var aQuery =
        new CategorySearchQuery(expectedPage,
            expectedPerPage,
            terms,
            expectedSort,
            expectedDirection);

    final var expectedPagination =
        new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

    final var expectedItemsCount = 2;
    final var expectedResult = expectedPagination.map(CategoryListOutput::from);

    when(categoryGateway.findAll(eq(aQuery))).thenReturn(expectedPagination);

    final var actualResult = useCase.execute(aQuery);

    assertEquals(expectedItemsCount, actualResult.items().size());
    assertEquals(expectedResult, actualResult);
    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(categories.size(), actualResult.total());
  }

  @Test
  void givenaValidQuery_whenHasNoResults_thenShouldReturnEmptyCategories() {
    final var categories = List.<Category>of();

    final var expectedPage = 0;
    final var expectedPerPage = 10;
    final var terms = "";
    final var expectedSort = "createdAt";
    final var expectedDirection = "asc";

    final var aQuery =
        new CategorySearchQuery(expectedPage,
            expectedPerPage,
            terms,
            expectedSort,
            expectedDirection);

    final var expectedPagination =
        new Pagination<>(expectedPage, expectedPerPage, categories.size(), categories);

    final var expectedItemsCount = 0;
    final var expectedResult = expectedPagination.map(CategoryListOutput::from);

    when(categoryGateway.findAll(eq(aQuery))).thenReturn(expectedPagination);

    final var actualResult = useCase.execute(aQuery);

    assertEquals(expectedItemsCount, actualResult.items().size());
    assertEquals(expectedResult, actualResult);
    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(categories.size(), actualResult.total());
  }

  @Test
  void givenaValidQuery_whenGatewayThrowsException_thenShouldReturnException() {
    final var expectedPage = 0;
    final var expectedPerPage = 10;
    final var terms = "";
    final var expectedSort = "createdAt";
    final var expectedDirection = "asc";
    final var expectedErrorMessage = "Gateway error";

    final var aQuery =
        new CategorySearchQuery(expectedPage,
            expectedPerPage,
            terms,
            expectedSort,
            expectedDirection);


    when(categoryGateway.findAll(eq(aQuery))).thenThrow(
        new IllegalStateException(expectedErrorMessage));

    final var actualException =
        Assertions.assertThrows(IllegalStateException.class, () -> useCase.execute(aQuery));

    assertEquals(expectedErrorMessage, actualException.getMessage());

  }

}
