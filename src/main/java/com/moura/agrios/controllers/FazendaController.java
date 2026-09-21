package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.models.Fazenda;
import com.moura.agrios.services.FazendaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/clientes/{clienteId}/fazendas")
public class FazendaController {

    private final FazendaService fazendaService;

    public FazendaController(FazendaService fazendaService) {

        this.fazendaService = fazendaService;
    }

    @PostMapping
    public ResponseEntity<Fazenda> cadastrar(@PathVariable Integer clienteId, @Valid @RequestBody Fazenda fazenda) {

        Fazenda fazendaSalva = fazendaService.cadastrar(clienteId,fazenda);
        return ResponseEntity.status(HttpStatus.CREATED).body(fazendaSalva);
    }

    @GetMapping
    public ResponseEntity<List<Fazenda>>listarPorCliente(@PathVariable Integer clienteId) {

        return ResponseEntity.ok(fazendaService.listarPorCliente(clienteId));
    }

    @GetMapping("/{fazendaId}")
    public ResponseEntity<Fazenda> buscarPorId(@PathVariable Integer clienteId,@PathVariable Integer fazendaId) {

        return ResponseEntity.ok(fazendaService.buscarPorId(clienteId,fazendaId));
    }
}