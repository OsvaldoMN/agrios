package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioContaReceberResponseDTO(
    List<RelatorioContaReceberItemDTO> itens,
    long totalContas,
    BigDecimal valorOriginalTotal,
    BigDecimal totalDescontos,
    BigDecimal valorFinalTotal,
    BigDecimal totalRecebido,
    BigDecimal saldoPendenteTotal
) {}
