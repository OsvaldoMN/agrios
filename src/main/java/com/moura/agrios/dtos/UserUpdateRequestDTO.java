package com.moura.agrios.dtos;

import com.moura.agrios.enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequestDTO(
    @NotBlank(message = "Username é obrigatório")
    String username,
    String password,
    @NotNull(message = "Role é obrigatório")
    UserRole role,
    Boolean ativo
) {
}
