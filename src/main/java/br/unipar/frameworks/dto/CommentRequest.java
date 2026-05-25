package br.unipar.frameworks.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentRequest(
    @NotBlank(message = "Texto do comentário é obrigatório")
    String text,

    @NotNull(message = "ID do produto é obrigatório")
    Long productId
) {}
