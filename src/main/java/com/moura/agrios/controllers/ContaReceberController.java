package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.dtos.*;
import com.moura.agrios.services.ContaReceberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/contas-receber")
public class ContaReceberController {

    private final ContaReceberService contaReceberService;

    public ContaReceberController(ContaReceberService contaReceberService) {
        this.contaReceberService = contaReceberService;
    }

    // CONTA MANUAL
    @PostMapping
    public ResponseEntity<ContaReceberResponse> cadastrarManual(@Valid @RequestBody CriarContaReceberRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(contaReceberService.cadastrarManual(request));
    }

    // REGISTRAR PAGAMENTO
    @PostMapping("/{id}/pagamentos")
    public ResponseEntity<ContaReceberResponse>registrarPagamento(@PathVariable Integer id, @Valid @RequestBody RegistrarRecebimentoRequest request) {

        return ResponseEntity.ok(contaReceberService.registrarRecebimento(id,request));
    }

    // LISTAR
    @GetMapping
    public ResponseEntity<List<ContaReceberResponse>> listarTodas() {

        return ResponseEntity.ok(contaReceberService.listarTodas());
    }

    // BUSCAR
    @GetMapping("/{id}")
    public ResponseEntity<ContaReceberResponse>buscarPorId(@PathVariable Integer id) {

        return ResponseEntity.ok(contaReceberService.buscarPorId(id));
    }
}