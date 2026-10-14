package com.moura.agrios.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record AplicarDescontoRequest(

    @NotNull(message = "Valor do desconto deve ser maior ou igual a zero")
    @DecimalMin(value = "0.01", message = "Desconto deve ser maior que zero")
    BigDecimal valor
    
) {}