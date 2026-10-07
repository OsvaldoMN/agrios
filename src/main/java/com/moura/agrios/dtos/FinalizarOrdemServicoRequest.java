package com.moura.agrios.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record FinalizarOrdemServicoRequest(

    @NotNull(message = "Data de vencimento é obrigatória")
    LocalDate dataVencimento

) {}