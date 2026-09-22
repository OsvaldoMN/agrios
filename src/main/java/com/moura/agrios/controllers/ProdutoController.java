
package com.moura.agrios.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.moura.agrios.models.Produto;
import com.moura.agrios.services.ProdutoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @PostMapping
    public ResponseEntity<Produto> cadastrar(@Valid @RequestBody Produto produto) {

        Produto salvo = produtoService.cadastrar(produto);

        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(produtoService.listarTodos());
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Produto>> listarAtivos() {
        return ResponseEntity.ok(produtoService.listarAtivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Integer id, @Valid @RequestBody Produto produto) {

        return ResponseEntity.ok(produtoService.atualizar(id, produto));
    }

    /*@PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(
            @PathVariable Integer id) {

        produtoService.desativar(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(
            @PathVariable Integer id) {

        produtoService.reativar(id);

        return ResponseEntity.noContent().build();
    }*/
}