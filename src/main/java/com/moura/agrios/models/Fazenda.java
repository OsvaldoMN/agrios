package com.moura.agrios.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.moura.agrios.enums.TipoIE;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "fazendas")
public class Fazenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Nome da fazenda é obrigatório")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "inscricao_estadual", length = 30)
    private String inscricaoEstadual;

    @NotNull(message = "Indicador de IE é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "indicador_ie", nullable = false)
    private TipoIE tipoIe;

    @Pattern(
        regexp = "^\\d{8}$",
        message = "CEP deve possuir 8 dígitos"
    )
    @Column(name = "cep", length = 8)
    private String cep;

    @Column(name = "estado", length = 2)
    private String estado;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "logradouro")
    private String logradouro;

    @Column(name = "numero")
    private String numero;

    @Column(name = "complemento")
    private String complemento;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "cliente_id",
        nullable = false
    )
    @JsonBackReference
    private Cliente cliente;

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