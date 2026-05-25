package br.unipar.frameworks.dto;

import br.unipar.frameworks.model.Comment;

public record CommentResponse(Long id, String text, Long productId) {
    public static CommentResponse fromEntity(Comment comment) {
        if (comment == null) return null;
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getProduct() != null ? comment.getProduct().getId() : null
        );
    }
}
