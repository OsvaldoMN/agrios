package com.moura.agrios.dtos;

import com.moura.agrios.enums.UserRole;

public record RegisterDTO(String username, String password, UserRole role) {

}
