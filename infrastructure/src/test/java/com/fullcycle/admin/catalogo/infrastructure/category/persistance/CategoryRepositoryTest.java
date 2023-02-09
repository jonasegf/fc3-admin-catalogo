package com.fullcycle.admin.catalogo.infrastructure.category.persistance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.MySQLGatewayTest;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import org.hibernate.PropertyValueException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;

@MySQLGatewayTest
public class CategoryRepositoryTest {
  @Autowired
  private CategoryRepository categoryRepository;

  @Test
  void givenAnInvalidNullName_whenCallsSave_shouldReturnError() {
    final var expectedPropertyName = "name";
    final var expectedMessage = "not-null property references a null or transient value : " +
        "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.name";

    final var aCategory = Category.newCategory("Filmes", "A categoria", true);

    final var anEntity = CategoryJpaEntity.from(aCategory);
    anEntity.setName(null);

    final var actualException =
        Assertions.assertThrows(DataIntegrityViolationException.class,
            () -> categoryRepository.save(anEntity));

    final var actualCause =
        Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

    assertEquals(expectedPropertyName, actualCause.getPropertyName());
    assertEquals(expectedMessage, actualCause.getMessage());
  }

  @Test
  void givenAnInvalidNullCreatedAt_whenCallsSave_shouldReturnError() {
    final var expectedPropertyName = "createdAt";
    final var expectedMessage = "not-null property references a null or transient value : " +
        "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.createdAt";

    final var aCategory = Category.newCategory("Filmes", "A categoria", true);

    final var anEntity = CategoryJpaEntity.from(aCategory);
    anEntity.setCreatedAt(null);

    final var actualException =
        Assertions.assertThrows(DataIntegrityViolationException.class,
            () -> categoryRepository.save(anEntity));

    final var actualCause =
        Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

    assertEquals(expectedPropertyName, actualCause.getPropertyName());
    assertEquals(expectedMessage, actualCause.getMessage());
  }

  @Test
  void givenAnInvalidNullUpdatedAt_whenCallsSave_shouldReturnError() {
    final var expectedPropertyName = "updatedAt";
    final var expectedMessage = "not-null property references a null or transient value : " +
        "com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity.updatedAt";

    final var aCategory = Category.newCategory("Filmes", "A categoria", true);

    final var anEntity = CategoryJpaEntity.from(aCategory);
    anEntity.setUpdatedAt(null);

    final var actualException =
        Assertions.assertThrows(DataIntegrityViolationException.class,
            () -> categoryRepository.save(anEntity));

    final var actualCause =
        Assertions.assertInstanceOf(PropertyValueException.class, actualException.getCause());

    assertEquals(expectedPropertyName, actualCause.getPropertyName());
    assertEquals(expectedMessage, actualCause.getMessage());
  }
}
