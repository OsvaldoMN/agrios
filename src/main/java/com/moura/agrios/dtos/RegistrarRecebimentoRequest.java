package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.moura.agrios.enums.FormaPagamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record RegistrarRecebimentoRequest(

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    BigDecimal valor,

    @NotNull(message = "Forma de pagamento é obrigatória")
    FormaPagamento formaPagamento,

    LocalDate dataPagamento,

    String observacoes

) {}