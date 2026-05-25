package br.unipar.frameworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "Nome do produto é obrigatório")
    String name,

    String description,

    @NotNull(message = "Preço é obrigatório")
    @PositiveOrZero(message = "Preço deve ser maior ou igual a zero")
    BigDecimal price
) {}
