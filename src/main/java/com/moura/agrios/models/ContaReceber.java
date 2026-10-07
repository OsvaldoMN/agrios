package com.moura.agrios.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.moura.agrios.enums.OrigemContaReceber;
import com.moura.agrios.enums.StatusContaReceber;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "contas_receber")
public class ContaReceber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    //pode null quando for conta manual
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ordem_servico_id", unique = true)
    private OrdemServico ordemServico;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false, length = 30)
    private OrigemContaReceber origem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusContaReceber status;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;


    //valor total
    @Column(name = "valor",nullable = false,precision = 15,scale = 2)
    private BigDecimal valor;


    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {

        if (status == null) {
            status = StatusContaReceber.ABERTA;
        }

        if (dataEmissao == null) {
            dataEmissao = LocalDate.now(ZoneId.of("America/Sao_Paulo"));
        }

        if (criadoEm == null) {
            criadoEm = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
        }
    }
}