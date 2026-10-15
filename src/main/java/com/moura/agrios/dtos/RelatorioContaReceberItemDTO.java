package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moura.agrios.enums.OrigemContaReceber;
import com.moura.agrios.enums.StatusContaReceber;

public record RelatorioContaReceberItemDTO(
    Integer id,
    String clienteNome,
    OrigemContaReceber origem,
    Integer ordemServicoId,
    String descricao,
    LocalDate dataEmissao,
    LocalDate dataVencimento,
    StatusContaReceber status,
    BigDecimal valorOriginal,
    BigDecimal valorDesconto,
    BigDecimal valorFinal,
    BigDecimal valorRecebido,
    BigDecimal saldo
) {}
