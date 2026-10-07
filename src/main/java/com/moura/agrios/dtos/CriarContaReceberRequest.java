package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarContaReceberRequest(

    @NotNull(message = "Cliente é obrigatório")
    Integer clienteId,

    @NotBlank(message = "Descrição é obrigatória")
    String descricao,

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01",message = "Valor deve ser maior que zero")
    BigDecimal valor,

    @NotNull(message = "Data de vencimento é obrigatória")
    LocalDate dataVencimento,

    String observacoes
) {}