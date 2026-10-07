package com.moura.agrios.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record CadastrarOrdemServicoRequest(

    @NotNull(message = "Cliente é obrigatório")
    Integer clienteId,

    @NotNull(message = "Fazenda é obrigatória")
    Integer fazendaId,

    @NotNull(message = "Serviço é obrigatório")
    Integer servicoId,

    Integer produtoId,

    @NotNull(message = "Data de início é obrigatória")
    LocalDate dataInicio,

    LocalDate dataFim,

    @DecimalMin(value = "0.01",message = "Hectares deve ser maior que zero")
    @Digits(integer = 10, fraction = 2)
    BigDecimal hectares,

    @NotEmpty(message = "Informe pelo menos uma máquina")
    List<@Valid MaquinaOSRequest> maquinas,

    String observacoes

) {}