package com.moura.agrios.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.dtos.UserResponseDTO;
import com.moura.agrios.dtos.UserUpdateRequestDTO;
import com.moura.agrios.models.Users;
import com.moura.agrios.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> listarTodos() {
        return userRepository.findAll().stream()
                .map(UserResponseDTO::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponseDTO buscarPorId(String id) {
        return userRepository.findById(id)
                .map(UserResponseDTO::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
    }

    @Transactional
    public UserResponseDTO atualizar(String id, UserUpdateRequestDTO dados) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        String username = dados.username().trim();

        if (userRepository.existsByUsernameIgnoreCaseAndIdNot(username, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro usuário com este nome de usuário");
        }

        user.setUsername(username);
        user.setRole(dados.role());

        if (dados.ativo() != null) {
            user.setAtivo(dados.ativo());
        }

        if (dados.password() != null && !dados.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(dados.password()));
        }

        Users salvo = userRepository.save(user);
        return new UserResponseDTO(salvo);
    }
}
