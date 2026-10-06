
package com.moura.agrios.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.moura.agrios.enums.StatusOS;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fazenda_id", nullable = false)
    private Fazenda fazenda;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;


    //opcional para serviços sem produtos (ex: desmate)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;


    @Column(name = "data_fim") //null até finalizar OS
    private LocalDate dataFim;


    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusOS status = StatusOS.ABERTA;


    @Column(name = "hectares", precision = 12, scale = 2)
    private BigDecimal hectares;


    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MaquinaOS> maquinas = new ArrayList<>();


    @Column(name = "valor_total", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;


    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;


    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;


    public void adicionarMaquina(MaquinaOS maquinaOS) {

        maquinas.add(maquinaOS);

        maquinaOS.setOrdemServico(this);
    }

    @PrePersist
    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now(
                ZoneId.of("America/Sao_Paulo")
            );
        }
        if (status == null) {
            status = StatusOS.ABERTA;
        }
    }
}