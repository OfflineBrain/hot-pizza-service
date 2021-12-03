package com.offlinebrain.hotpizza.rest.mapper.entity;

import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.rest.model.category.CategoryDTO;
import com.offlinebrain.hotpizza.rest.model.category.CreateCategoryDTO;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
public class CategoryMapper {
    public CategoryDTO productCategoryToCategoryDto(ProductCategory category) {
        return CategoryDTO.builder()
                .uuid(category.getUuid())
                .name(category.getName())
                .parent(Optional.ofNullable(category.getParent()).map(ProductCategory::getUuid).orElse(null))
                .build();
    }

    public ProductCategory categoryDtoToProductCategory(CategoryDTO dto) {
        return ProductCategory.builder()
                .uuid(dto.getUuid())
                .name(dto.getName())
                .parent(uuidToProductCategory(dto.getParent()))
                .build();
    }

    public ProductCategory createCategoryDtoToProductCategory(CreateCategoryDTO dto) {
        return ProductCategory.builder()
                .name(dto.getName())
                .parent(uuidToProductCategory(dto.getParent()))
                .build();
    }

    private ProductCategory uuidToProductCategory(UUID uuid) {
        if (Objects.isNull(uuid)) {
            return null;
        }
        return ProductCategory.builder().uuid(uuid).build();
    }
}
