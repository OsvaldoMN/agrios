package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moura.agrios.enums.StatusOS;

public record RelatorioTrabalhoFazendaItemDTO(
    String clienteNome,
    String fazendaNome,
    Integer ordemServicoId,
    String servicoNome,
    LocalDate dataInicio,
    LocalDate dataFim,
    long diasTrabalho,
    BigDecimal hectares,
    StatusOS status
) {}
