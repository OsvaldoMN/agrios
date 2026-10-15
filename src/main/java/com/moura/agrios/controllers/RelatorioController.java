package com.moura.agrios.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moura.agrios.dtos.RelatorioContaReceberResponseDTO;
import com.moura.agrios.dtos.RelatorioOSResponseDTO;
import com.moura.agrios.dtos.RelatorioTrabalhoFazendaResponseDTO;
import com.moura.agrios.enums.StatusContaReceber;
import com.moura.agrios.enums.StatusOS;
import com.moura.agrios.services.RelatorioService;

@RestController
@RequestMapping("/relatorios/api")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/ordens-servico")
    public ResponseEntity<RelatorioOSResponseDTO> relatorioOrdensServico(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) Integer clienteId,
        @RequestParam(required = false) Integer fazendaId,
        @RequestParam(required = false) Integer servicoId,
        @RequestParam(required = false) Integer produtoId,
        @RequestParam(required = false) StatusOS status
    ) {
        RelatorioOSResponseDTO response = relatorioService.gerarRelatorioOS(
            dataInicio, dataFim, clienteId, fazendaId, servicoId, produtoId, status
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/contas-receber")
    public ResponseEntity<RelatorioContaReceberResponseDTO> relatorioContasReceber(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) Integer clienteId,
        @RequestParam(required = false) StatusContaReceber status
    ) {
        RelatorioContaReceberResponseDTO response = relatorioService.gerarRelatorioContasReceber(
            dataInicio, dataFim, clienteId, status
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/trabalho-fazenda")
    public ResponseEntity<RelatorioTrabalhoFazendaResponseDTO> relatorioTrabalhoFazenda(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(required = false) Integer clienteId,
        @RequestParam(required = false) Integer fazendaId
    ) {
        RelatorioTrabalhoFazendaResponseDTO response = relatorioService.gerarRelatorioTrabalhoFazenda(
            dataInicio, dataFim, clienteId, fazendaId
        );
        return ResponseEntity.ok(response);
    }
}
