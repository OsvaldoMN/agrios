
package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.models.Maquina;
import com.moura.agrios.services.MaquinaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/maquinas")
public class MaquinaController {

    private final MaquinaService maquinaService;

    public MaquinaController(MaquinaService maquinaService) {
        this.maquinaService = maquinaService;
    }

    @PostMapping
    public ResponseEntity<Maquina> cadastrar(@Valid @RequestBody Maquina maquina) {
        Maquina salva = maquinaService.cadastrar(maquina);

        return ResponseEntity.status(HttpStatus.CREATED).body(salva);
    }

    @GetMapping
    public ResponseEntity<List<Maquina>> listarTodas() {

        return ResponseEntity.ok(maquinaService.listarTodas());
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Maquina>> listarAtivas() {

        return ResponseEntity.ok(maquinaService.listarAtivas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Maquina> buscarPorId(@PathVariable Integer id) {

        return ResponseEntity.ok(maquinaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Maquina> atualizar(@PathVariable Integer id,@Valid @RequestBody Maquina maquina) {

        return ResponseEntity.ok(maquinaService.atualizar(id, maquina));
    }

    /*@PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Integer id) {

        maquinaService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Integer id) {

        maquinaService.reativar(id);

        return ResponseEntity.noContent().build();
    }*/
}