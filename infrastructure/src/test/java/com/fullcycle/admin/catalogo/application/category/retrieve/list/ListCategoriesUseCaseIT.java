package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.IntegrationTest;
import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@IntegrationTest
public class ListCategoriesUseCaseIT {

  @Autowired
  private ListCategoriesUseCase useCase;

  @Autowired
  private CategoryRepository categoryRepository;

  @BeforeEach
  void mockUp() {
    final var categories = Stream.of(
        Category.newCategory("Filmes", null, true),
        Category.newCategory("Netflix Originais", "Autorais netflix", true),
        Category.newCategory("Amazon Originais", "Autorais amazon", true),
        Category.newCategory("Docs", null, true),
        Category.newCategory("Esportes", null, true),
        Category.newCategory("Kids", null, true),
        Category.newCategory("Series", null, true)
    ).map(CategoryJpaEntity::from).toList();
    categoryRepository.saveAll(categories);
  }
}
