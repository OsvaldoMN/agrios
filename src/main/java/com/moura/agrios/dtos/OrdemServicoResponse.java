package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.moura.agrios.enums.StatusOS;

public record OrdemServicoResponse(

    Integer id,

    Integer clienteId,
    String clienteNome,

    Integer fazendaId,
    String fazendaNome,

    Integer servicoId,
    String servicoNome,

    Integer produtoId,
    String produtoNome,

    LocalDate dataInicio,
    LocalDate dataFim,
    LocalDateTime criadoEm,

    StatusOS status,

    BigDecimal hectares,

    List<MaquinaOSResponse> maquinas,

    BigDecimal valorTotal,

    String observacoes

) {}