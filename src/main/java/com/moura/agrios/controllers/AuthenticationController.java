package com.moura.agrios.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moura.agrios.dtos.AuthenticationDTO;
import com.moura.agrios.dtos.LoginResponseDTO;
import com.moura.agrios.dtos.RegisterDTO;
import com.moura.agrios.infra.security.TokenService;
import com.moura.agrios.models.Users;
import com.moura.agrios.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController 
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired 
    private AuthenticationManager authenticationManager;

    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
        try{
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.username(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generatedToken((Users) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));

            }catch(Exception e){
            e.printStackTrace(); //mostra o erro que deu
                    return ResponseEntity.status(401).body(e.getMessage());
            }
    }

    @PostMapping("/register") //comentar endpoint futuramente pois ele não deve existir
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.userRepository.findByUsername(data.username()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Users newUser = new Users(data.username(), encryptedPassword, data.role());

        this.userRepository.save(newUser);
        return ResponseEntity.ok().build();
    }

}
