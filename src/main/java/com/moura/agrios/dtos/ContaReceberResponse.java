package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.moura.agrios.enums.OrigemContaReceber;
import com.moura.agrios.enums.StatusContaReceber;

public record ContaReceberResponse(

    Integer id,

    Integer clienteId,
    String clienteNome,

    Integer ordemServicoId,

    OrigemContaReceber origem,
    StatusContaReceber status,

    String descricao,

    BigDecimal valor,

    BigDecimal valorRecebido,

    BigDecimal saldo,

    LocalDate dataEmissao,
    LocalDate dataVencimento,

    List<RecebimentoResponse> recebimentos,

    String observacoes,

    LocalDateTime criadoEm

) {}