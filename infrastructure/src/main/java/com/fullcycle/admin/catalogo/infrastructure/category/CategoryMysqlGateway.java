package com.fullcycle.admin.catalogo.infrastructure.category;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class CategoryMysqlGateway implements CategoryGateway {
  private final CategoryRepository repository;

  public CategoryMysqlGateway(final CategoryRepository repository) {
    this.repository = Objects.requireNonNull(repository);
  }

  @Override
  public Category create(final Category aCategory) {
    return save(aCategory);
  }

  @Override
  public void deleteById(final CategoryId anId) {

  }

  @Override
  public Optional<Category> findById(final CategoryId anId) {
    return Optional.empty();
  }

  @Override
  public Category update(final Category aCategory) {
    return save(aCategory);
  }

  @Override
  public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
    return null;
  }

  private Category save(final Category aCategory) {
    return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
  }
}
