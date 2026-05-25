package br.unipar.frameworks.dto;

import br.unipar.frameworks.model.Product;
import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price) {
    public static ProductResponse fromEntity(Product product) {
        if (product == null) return null;
        return new ProductResponse(product.getId(), product.getName(), product.getDescription(), product.getPrice());
    }
}
