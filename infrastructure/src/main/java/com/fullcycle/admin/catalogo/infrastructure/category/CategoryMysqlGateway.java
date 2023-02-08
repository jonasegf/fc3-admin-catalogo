package com.fullcycle.admin.catalogo.infrastructure.category;

import static com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils.like;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.by;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryGateway;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import com.fullcycle.admin.catalogo.domain.category.CategorySearchQuery;
import com.fullcycle.admin.catalogo.domain.pagination.Pagination;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryJpaEntity;
import com.fullcycle.admin.catalogo.infrastructure.category.persistence.CategoryRepository;
import com.fullcycle.admin.catalogo.infrastructure.utils.SpecificationUtils;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
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
    final var anIdValue = anId.getValue();
    if (this.repository.existsById(anIdValue)) {
      this.repository.deleteById(anIdValue);
    }

  }

  @Override
  public Optional<Category> findById(final CategoryId anId) {
    return this.repository.findById(anId.getValue())
        .map(CategoryJpaEntity::toAggregate);
  }

  @Override
  public Category update(final Category aCategory) {
    return save(aCategory);
  }

  @Override
  public Pagination<Category> findAll(final CategorySearchQuery aQuery) {
    final var page = PageRequest.of(
        aQuery.page(),
        aQuery.perPage(),
        by(Direction.fromString(aQuery.direction()), aQuery.sort()));

    final var specifications = Optional.ofNullable(aQuery.terms())
        .filter(str -> !str.isBlank())
        .map(str -> SpecificationUtils
            .<CategoryJpaEntity>like("name", str)
            .or(like("description", str))
        )
        .orElse(null);

    final var pageResult = this.repository.findAll(specifications, page);

    return new Pagination<>(
        pageResult.getNumber(),
        pageResult.getSize(),
        pageResult.getTotalElements(),
        pageResult.map(CategoryJpaEntity::toAggregate).toList());
  }

  private Category save(final Category aCategory) {
    return this.repository.save(CategoryJpaEntity.from(aCategory)).toAggregate();
  }
}
