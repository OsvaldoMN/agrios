package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioOSResponseDTO(
    List<RelatorioOSItemDTO> itens,
    long totalOrdens,
    BigDecimal totalHectares,
    BigDecimal valorTotal
) {}
