package com.moura.agrios.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public record MaquinaOSRequest(

    @NotNull(message = "Informe a máquina")
    Integer maquinaId,

    @NotNull(message = "Informe o valor da máquina")
    @DecimalMin(value = "0.01", message = "O valor da máquina deve ser maior que zero")
    @Digits(integer = 13, fraction = 2)
    BigDecimal valor

) {}