
package com.moura.agrios.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.models.Servico;
import com.moura.agrios.repositories.ServicoRepository;

@Service
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoService(ServicoRepository servicoRepository) {
        this.servicoRepository = servicoRepository;
    }

    @Transactional
    public Servico cadastrar(Servico servico) {

        String nome = normalizarNome(servico.getNome());

        if (servicoRepository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um serviço com este nome");
        }

        Servico novo = new Servico();

        novo.setNome(nome);
        novo.setDescricao(servico.getDescricao());
        novo.setAtivo(true);

        return servicoRepository.save(novo);
    }

    @Transactional(readOnly = true)
    public List<Servico> listarTodos() {
        return servicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Servico> listarAtivos() {
        return servicoRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Servico buscarPorId(Integer id) {

        return servicoRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Serviço não encontrado"));
    }

    @Transactional
    public Servico atualizar(Integer id, Servico dados) {

        Servico servico = buscarPorId(id);

        String nome = normalizarNome(dados.getNome());

        if (servicoRepository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe outro serviço com este nome");
        }

        servico.setNome(nome);
        servico.setDescricao(dados.getDescricao());
        servico.setAtivo(dados.getAtivo());

        return servicoRepository.save(servico);
    }

   /* @Transactional
    public void desativar(Integer id) {

        Servico servico = buscarPorId(id);

        servico.setAtivo(false);

        servicoRepository.save(servico);
    }

    @Transactional
    public void reativar(Integer id) {

        Servico servico = buscarPorId(id);

        servico.setAtivo(true);

        servicoRepository.save(servico);
    } */

    private String normalizarNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nome do serviço é obrigatório");
        }

        return nome.trim();
    }
}