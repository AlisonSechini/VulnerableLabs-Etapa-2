package br.unipar.frameworks.dto;

import br.unipar.frameworks.model.User;

public record UserResponse(Long id, String name, String email, String role) {
    public static UserResponse fromEntity(User user) {
        if (user == null) return null;
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
