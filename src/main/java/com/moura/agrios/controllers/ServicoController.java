
package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.models.Servico;
import com.moura.agrios.services.ServicoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    private final ServicoService servicoService;

    public ServicoController(ServicoService servicoService) {
        this.servicoService = servicoService;
    }

    @PostMapping
    public ResponseEntity<Servico> cadastrar(@Valid @RequestBody Servico servico) {

        Servico salvo = servicoService.cadastrar(servico);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Servico>> listarAtivos() {

        return ResponseEntity.ok(servicoService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servico> buscarPorId(@PathVariable Integer id) {

        return ResponseEntity.ok(servicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servico> atualizar(@PathVariable Integer id,@Valid @RequestBody Servico servico) {

        return ResponseEntity.ok(servicoService.atualizar(id, servico));
    }

    /*@PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Integer id) {

        servicoService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Integer id) {

        servicoService.reativar(id);

        return ResponseEntity.noContent().build();
    }*/
}