package com.fullcycle.admin.catalogo.infrastructure.category;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import java.util.List;
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

    assertEquals(0, categoryRepository.count());
  }

  @Test
  void givenInvalidCategoryId_whenTryToDeleteIt_shouldDeleteCategory() {
    assertEquals(0, categoryRepository.count());

    categoryGateway.deleteById(CategoryId.from("invalid"));

    assertEquals(0, categoryRepository.count());
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

  @Test
  void givenPrePersistedCategories_whenCallsFindAll_shouldReturnPaginated() {
    final var expectedPage = 0;
    final var expectedPerPage = 1;
    final var expectedTotal = 3;

    final var filmes = Category.newCategory("Filmes", null, true);
    final var animes = Category.newCategory("Animes", null, true);
    final var series = Category.newCategory("Series", null, true);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAll(List.of(
        CategoryJpaEntity.from(filmes),
        CategoryJpaEntity.from(animes),
        CategoryJpaEntity.from(series)
    ));

    assertEquals(3, categoryRepository.count());

    final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
    final var actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(animes.getId(), actualResult.items().get(0).getId());
  }

  @Test
  void givenEmptyCategoriesTable_whenCallsFindAll_shouldReturnEmptyPage() {
    final var expectedPage = 0;
    final var expectedPerPage = 1;
    final var expectedTotal = 0;

    assertEquals(0, categoryRepository.count());

    final var query = new CategorySearchQuery(0, 1, "", "name", "asc");
    final var actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(0, actualResult.items().size());
  }

  @Test
  void givenFollowPagination_whenCallsFindAllWithPage1_shouldReturnPaginated() {
    var expectedPage = 0;
    final var expectedPerPage = 1;
    final var expectedTotal = 3;

    final var filmes = Category.newCategory("Filmes", null, true);
    final var animes = Category.newCategory("Animes", null, true);
    final var series = Category.newCategory("Series", null, true);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAll(List.of(
        CategoryJpaEntity.from(filmes),
        CategoryJpaEntity.from(animes),
        CategoryJpaEntity.from(series)
    ));

    assertEquals(3, categoryRepository.count());

    //Page 0
    var query = new CategorySearchQuery(0, 1, "", "name", "asc");
    var actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(animes.getId(), actualResult.items().get(0).getId());

    //Page 1
    expectedPage = 1;

    query = new CategorySearchQuery(1, 1, "", "name", "asc");
    actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(filmes.getId(), actualResult.items().get(0).getId());

    //Page 2
    expectedPage = 2;

    query = new CategorySearchQuery(2, 1, "", "name", "asc");
    actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(series.getId(), actualResult.items().get(0).getId());
  }

  @Test
  void givenPrePersistedCategoriesAndAniAsTerms_whenCallsFindAllAndTermsMatchesCategoryName_shouldReturnPaginated() {
    final var expectedPage = 0;
    final var expectedPerPage = 1;
    final var expectedTotal = 1;

    final var filmes = Category.newCategory("Filmes", null, true);
    final var animes = Category.newCategory("Animes", null, true);
    final var series = Category.newCategory("Series", null, true);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAll(List.of(
        CategoryJpaEntity.from(filmes),
        CategoryJpaEntity.from(animes),
        CategoryJpaEntity.from(series)
    ));

    assertEquals(3, categoryRepository.count());

    final var query = new CategorySearchQuery(0, 1, "ani", "name", "asc");
    final var actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(animes.getId(), actualResult.items().get(0).getId());
  }

  @Test
  void givenPrePersistedCategoriesAndMaisAssistidaAsTerms_whenCallsFindAllAndTermsMatchesCategoryName_shouldReturnPaginated() {
    final var expectedPage = 0;
    final var expectedPerPage = 1;
    final var expectedTotal = 1;

    final var filmes = Category.newCategory("Filmes", "A categoria mais assistida", true);
    final var animes = Category.newCategory("Animes", "A categoria mais top", true);
    final var series = Category.newCategory("Series", "A mais paia", true);

    assertEquals(0, categoryRepository.count());

    categoryRepository.saveAll(List.of(
        CategoryJpaEntity.from(filmes),
        CategoryJpaEntity.from(animes),
        CategoryJpaEntity.from(series)
    ));

    assertEquals(3, categoryRepository.count());

    final var query = new CategorySearchQuery(0, 1, "MAIS ASSISTIDA", "name", "asc");
    final var actualResult = categoryGateway.findAll(query);

    assertEquals(expectedPage, actualResult.currentPage());
    assertEquals(expectedPerPage, actualResult.perPage());
    assertEquals(expectedTotal, actualResult.total());
    assertEquals(expectedPerPage, actualResult.items().size());
    assertEquals(filmes.getId(), actualResult.items().get(0).getId());
  }

}
