package com.moura.agrios.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.moura.agrios.enums.TipoPessoa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Tipo de pessoa é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoa tipoPessoa;

 
    @Column(name = "nome")
    private String nome;

    // Apenas tipopessoa = CNPJ
    @Column(name = "razao_social")
    private String razaoSocial;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @NotBlank(message = "Documento é obrigatório")
    @Column(
        name = "documento",
        nullable = false,
        unique = true,
        length = 14
    )
    private String documento;


    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;


    @Email(message = "Email inválido")
    @Column(name = "email", unique = true)
    private String email;


    @Pattern(
        regexp = "^\\d{10,11}$",
        message = "Contato deve possuir 10 ou 11 dígitos"
    )
    @Column(name = "contato", length = 11)
    private String contato;



    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(
        name = "criado_em",
        nullable = false,
        updatable = false
    )
    private LocalDateTime criadoEm;

    @OneToMany(
        mappedBy = "cliente",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    private List<Fazenda> fazendas = new ArrayList<>();

    @PrePersist
    public void prePersist() {

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }

        if (ativo == null) {
            ativo = true;
        }
    }
}