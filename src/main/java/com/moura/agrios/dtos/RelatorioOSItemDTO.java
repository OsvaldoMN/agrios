package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moura.agrios.enums.StatusOS;

public record RelatorioOSItemDTO(
    Integer id,
    LocalDate dataInicio,
    LocalDate dataFim,
    String clienteNome,
    String fazendaNome,
    String servicoNome,
    String produtoNome,
    BigDecimal hectares,
    StatusOS status,
    BigDecimal valorTotal
) {}
