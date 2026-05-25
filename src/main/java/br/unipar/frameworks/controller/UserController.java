package br.unipar.frameworks.controller;

import br.unipar.frameworks.dto.UserResponse;
import br.unipar.frameworks.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(UserResponse::fromEntity)
                .orElseThrow();
    }

    @GetMapping("/search-safe")
    public List<UserResponse> safeSearch(@RequestParam String term) {
        return userRepository.safeSearchByName(term).stream()
                .map(UserResponse::fromEntity)
                .toList();
    }

    @GetMapping("/search-unsafe")
    public List<UserResponse> unsafeSearch(@RequestParam String term) {
        String jpql = "select u from br.unipar.frameworks.model.User u where lower(u.name) like lower(concat('%', :term, '%'))";
        return entityManager.createQuery(jpql, br.unipar.frameworks.model.User.class)
                .setParameter("term", term)
                .getResultList()
                .stream()
                .map(UserResponse::fromEntity)
                .toList();
    }
}
