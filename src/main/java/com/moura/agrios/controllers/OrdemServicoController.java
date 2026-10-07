package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.dtos.CadastrarOrdemServicoRequest;
import com.moura.agrios.dtos.FinalizarOrdemServicoRequest;
import com.moura.agrios.dtos.OrdemServicoResponse;
import com.moura.agrios.services.OrdemServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(
            OrdemServicoService ordemServicoService) {

        this.ordemServicoService = ordemServicoService;
    }

    // CADASTRAR
    @PostMapping
    public ResponseEntity<OrdemServicoResponse> cadastrar(
            @Valid @RequestBody CadastrarOrdemServicoRequest request) {

        OrdemServicoResponse resposta =
            ordemServicoService.cadastrar(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(resposta);
    }

    // EDITAR
@PutMapping("/{id}")
public ResponseEntity<OrdemServicoResponse> atualizar(
        @PathVariable Integer id,
        @Valid @RequestBody CadastrarOrdemServicoRequest request) {

    OrdemServicoResponse resposta =
        ordemServicoService.atualizar(id, request);

    return ResponseEntity.ok(resposta);
}

    // FINALIZAR
    @PatchMapping("/{id}/finalizar")
    public ResponseEntity<OrdemServicoResponse> finalizar(@PathVariable Integer id, @Valid @RequestBody FinalizarOrdemServicoRequest request) {

        return ResponseEntity.ok(ordemServicoService.finalizar(id, request.dataVencimento()));
        }

    // CANCELAR
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<OrdemServicoResponse> cancelar(
            @PathVariable Integer id) {

        OrdemServicoResponse resposta =
            ordemServicoService.cancelar(id);

        return ResponseEntity.ok(resposta);
    }

    // LISTAR
    @GetMapping
    public ResponseEntity<List<OrdemServicoResponse>> listarTodas() {

        return ResponseEntity.ok(
            ordemServicoService.listarTodas()
        );
    }

    // BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
            ordemServicoService.buscarPorId(id)
        );
    }
}