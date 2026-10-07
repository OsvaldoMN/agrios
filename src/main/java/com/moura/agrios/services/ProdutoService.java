
package com.moura.agrios.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.models.Produto;
import com.moura.agrios.repositories.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto cadastrar(Produto produto) {

        String nome = normalizarNome(produto.getNome());

        if (produtoRepository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe um produto com este nome");
        }

        Produto novo = new Produto();

        novo.setNome(nome);
        novo.setDescricao(produto.getDescricao());
        novo.setAtivo(true);

        return produtoRepository.save(novo);
    }

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarAtivos() {
        return produtoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Integer id) {
        return produtoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Produto não encontrado"));
    }

    @Transactional
    public Produto atualizar(Integer id, Produto dados) {

        Produto produto = buscarPorId(id);

        String nome = normalizarNome(dados.getNome());

        if (produtoRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe outro produto com este nome");
        }

        produto.setNome(nome);
        produto.setDescricao(dados.getDescricao());
        produto.setAtivo(dados.getAtivo());

        return produtoRepository.save(produto);
    }

    /*@Transactional
    public void desativar(Integer id) {

        Produto produto = buscarPorId(id);

        produto.setAtivo(false);

        produtoRepository.save(produto);
    }

    @Transactional
    public void reativar(Integer id) {

        Produto produto = buscarPorId(id);

        produto.setAtivo(true);

        produtoRepository.save(produto);
    }*/

    private String normalizarNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nome do produto é obrigatório"
            );
        }
        return nome.trim();
    }
}