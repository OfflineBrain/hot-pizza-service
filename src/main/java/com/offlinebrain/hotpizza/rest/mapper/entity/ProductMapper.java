package com.offlinebrain.hotpizza.rest.mapper.entity;

import com.offlinebrain.hotpizza.data.model.Product;
import com.offlinebrain.hotpizza.data.model.ProductCategory;
import com.offlinebrain.hotpizza.rest.model.product.CreateProductDTO;
import com.offlinebrain.hotpizza.rest.model.product.ProductModel;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class ProductMapper {
    public ProductModel productToProductDto(Product product) {
        return ProductModel.builder()
                .name(product.getName())
                .description(product.getDescription())
                .amount(product.getAmount())
                .amountUnit(product.getAmountUnit())
                .price(product.getPrice())
                .uuid(product.getUuid())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public Product createProductDtoToProduct(CreateProductDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .category(uuidToProductCategory(dto.getCategoryUUID()))
                .amount(dto.getAmount())
                .amountUnit(dto.getAmountUnit())
                .price(dto.getPrice())
                .build();
    }

    private ProductCategory uuidToProductCategory(UUID uuid) {
        if (Objects.isNull(uuid)) {
            return null;
        }
        return ProductCategory.builder().uuid(uuid).build();
    }
}
