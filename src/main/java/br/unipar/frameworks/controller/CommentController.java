package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.CommentRequest;
import br.unipar.frameworks.dto.CommentResponse;
import br.unipar.frameworks.model.Comment;
import br.unipar.frameworks.model.Product;
import br.unipar.frameworks.repository.CommentRepository;
import br.unipar.frameworks.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;

    public CommentController(CommentRepository commentRepository, ProductRepository productRepository) {
        this.commentRepository = commentRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/product/{productId}")
    public List<CommentResponse> listByProduct(@PathVariable Long productId) {
        return commentRepository.findByProductId(productId).stream()
                .map(CommentResponse::fromEntity)
                .toList();
    }

    @PostMapping
    public CommentResponse create(@Valid @RequestBody CommentRequest request) {
        Product product = productRepository.findById(request.productId()).orElseThrow();
        Comment comment = new Comment();
        comment.setText(request.text());
        comment.setProduct(product);
        Comment saved = commentRepository.save(comment);
        return CommentResponse.fromEntity(saved);
    }
}
