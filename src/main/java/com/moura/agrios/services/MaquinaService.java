
package com.moura.agrios.services;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.models.Maquina;
import com.moura.agrios.repositories.MaquinaRepository;

@Service
public class MaquinaService {

    private final MaquinaRepository maquinaRepository;

    public MaquinaService(MaquinaRepository maquinaRepository) {
        this.maquinaRepository = maquinaRepository;
    }

    @Transactional
    public Maquina cadastrar(Maquina maquina) {

        String nome = normalizarNome(maquina.getNome());

        String identificacao = normalizarIdentificacao(maquina.getIdentificacao());

        if (identificacao != null && maquinaRepository.existsByIdentificacaoIgnoreCase(identificacao)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma máquina com esta identificação");
        }

        Maquina nova = new Maquina();

        nova.setNome(nome);
        nova.setIdentificacao(identificacao);
        nova.setDescricao(maquina.getDescricao());
        nova.setMarca(maquina.getMarca());
        nova.setModelo(maquina.getModelo());
        nova.setAnoFabricacao(maquina.getAnoFabricacao());
        nova.setPlaca(maquina.getPlaca());
        nova.setNumeroSerie(maquina.getNumeroSerie());
        nova.setAtivo(true);

        return maquinaRepository.save(nova);
    }

    @Transactional(readOnly = true)
    public List<Maquina> listarTodas() {
        return maquinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Maquina> listarAtivas() {
        return maquinaRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Maquina buscarPorId(Integer id) {
        return maquinaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Máquina não encontrada"));
    }

    @Transactional
    public Maquina atualizar(Integer id, Maquina dados) {

        Maquina maquina = buscarPorId(id);

        String nome = normalizarNome(dados.getNome());

        String identificacao = normalizarIdentificacao(dados.getIdentificacao());

        if (identificacao != null && maquinaRepository.existsByIdentificacaoIgnoreCaseAndIdNot(identificacao,id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe outra máquina com esta identificação");
        }

        maquina.setNome(nome);
        maquina.setIdentificacao(identificacao);
        maquina.setDescricao(dados.getDescricao());
        maquina.setMarca(dados.getMarca());
        maquina.setModelo(dados.getModelo());
        maquina.setAnoFabricacao(dados.getAnoFabricacao());
        maquina.setPlaca(dados.getPlaca());
        maquina.setNumeroSerie(dados.getNumeroSerie());
        maquina.setAtivo(dados.getAtivo());

        return maquinaRepository.save(maquina);
    }

    /*@Transactional
    public void desativar(Integer id) {

        Maquina maquina = buscarPorId(id);

        maquina.setAtivo(false);

        maquinaRepository.save(maquina);
    }

    @Transactional
    public void reativar(Integer id) {

        Maquina maquina = buscarPorId(id);

        maquina.setAtivo(true);

        maquinaRepository.save(maquina);
    }*/

    private String normalizarNome(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nome da máquina é obrigatório");
        }
        return nome.trim();
    }

    private String normalizarIdentificacao(String identificacao) {
        if (identificacao == null || identificacao.isBlank()) {
            return null;
        }
        return identificacao.trim().toUpperCase();
    }
}