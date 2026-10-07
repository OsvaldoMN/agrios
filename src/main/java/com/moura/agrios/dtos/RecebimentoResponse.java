package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.moura.agrios.enums.FormaPagamento;

public record RecebimentoResponse(

    Integer id,

    BigDecimal valor,

    FormaPagamento formaPagamento,

    LocalDate dataPagamento,

    String observacoes,

    LocalDateTime criadoEm

) {}