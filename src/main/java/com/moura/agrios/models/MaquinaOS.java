
package com.moura.agrios.models;

import java.math.BigDecimal;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "maquinas_os", uniqueConstraints = { @UniqueConstraint(name = "uk_maquinas_os_os_maquina", columnNames = {"ordem_servico_id", "maquina_id"})})
public class MaquinaOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /*
     * Uma OS pode possuir várias máquinas.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ordem_servico_id", nullable = false)
    private OrdemServico ordemServico;

    /*
     * Uma máquina pode participar de várias OS.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "maquina_id", nullable = false)
    private Maquina maquina;

    /*
     * Valor cobrado por esta máquina nesta OS.
     */
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
}