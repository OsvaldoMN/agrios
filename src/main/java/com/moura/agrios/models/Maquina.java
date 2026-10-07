
package com.moura.agrios.models;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

@Getter
@Setter
@Entity
@Table(name = "maquinas")
public class Maquina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Nome da máquina é obrigatório")
    @Size(max = 150)
    @Column(name = "nome", nullable = false, length = 150)
    private String nome;

    //cod interno = MOTRIZ-01, PA-02...
    @Size(max = 80)
    @Column(name = "identificacao", unique = true, length = 80)
    private String identificacao;

    @Size(max = 100)
    @Column(name = "marca", length = 100)
    private String marca;

    @Size(max = 100)
    @Column(name = "modelo", length = 100)
    private String modelo;

    @Min(value = 1900, message = "Ano de fabricação inválido")
    @Column(name = "ano_fabricacao")
    private Integer anoFabricacao;

    @Size(max = 10)
    @Column(name = "placa", length = 10)
    private String placa;

    @Size(max = 100)
    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Setter(AccessLevel.NONE)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        }
        if (ativo == null) {
            ativo = true;
        }
    }
}