package com.fullcycle.admin.catalogo.application.category.retrieve.list;

import com.fullcycle.admin.catalogo.domain.category.Category;
import com.fullcycle.admin.catalogo.domain.category.CategoryId;
import java.time.Instant;

public record CategoryListOutput(
    CategoryId id,
    String name,
    String description,
    boolean isActive,
    Instant createdAt,
    Instant deletedAt
) {

  public static CategoryListOutput from(final Category aCategory) {
    return new CategoryListOutput(
        aCategory.getId(),
        aCategory.getName(),
        aCategory.getDescription(),
        aCategory.isActive(),
        aCategory.getCreatedAt(),
        aCategory.getDeletedAt());
  };

}
