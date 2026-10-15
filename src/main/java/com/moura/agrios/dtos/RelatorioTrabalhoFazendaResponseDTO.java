package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioTrabalhoFazendaResponseDTO(
    List<RelatorioTrabalhoFazendaItemDTO> itens,
    List<ResumoFazendaDTO> resumosPorFazenda,
    long totalOrdens,
    long totalDias,
    BigDecimal totalHectares
) {}
