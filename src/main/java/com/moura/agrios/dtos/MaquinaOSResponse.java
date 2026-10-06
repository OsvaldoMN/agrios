package com.moura.agrios.dtos;

import java.math.BigDecimal;

public record MaquinaOSResponse(

    Integer id,
    Integer maquinaId,
    String maquinaNome,
    String identificacao,
    BigDecimal valor

) {}