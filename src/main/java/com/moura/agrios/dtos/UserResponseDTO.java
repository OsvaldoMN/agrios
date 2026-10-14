package com.moura.agrios.dtos;

import com.moura.agrios.enums.UserRole;
import com.moura.agrios.models.Users;

public record UserResponseDTO(
    String id,
    String username,
    UserRole role,
    Boolean ativo
) {
    public UserResponseDTO(Users user) {
        this(user.getId(), user.getUsername(), user.getRole(), user.getAtivo());
    }
}
