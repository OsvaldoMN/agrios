package com.moura.agrios.dtos;

import java.math.BigDecimal;

public record ResumoFazendaDTO(
    String fazendaNome,
    String clienteNome,
    long quantidadeOrdens,
    long totalDias,
    BigDecimal totalHectares
) {}
