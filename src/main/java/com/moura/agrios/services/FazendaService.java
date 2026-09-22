package com.moura.agrios.services;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.moura.agrios.enums.TipoIE;
import com.moura.agrios.models.Cliente;
import com.moura.agrios.models.Fazenda;
import com.moura.agrios.repositories.FazendaRepository;

import jakarta.transaction.Transactional;

@Service
public class FazendaService {

    private final FazendaRepository fazendaRepository;
    private final ClienteService clienteService;

    public FazendaService(FazendaRepository fazendaRepository, ClienteService clienteService) {
        this.fazendaRepository = fazendaRepository;
        this.clienteService = clienteService;
    }

    public Fazenda cadastrar(Integer clienteId, Fazenda fazenda) {

        Cliente cliente = clienteService.buscarPorId(clienteId);

        validarFazenda(fazenda, null);

        fazenda.setCliente(cliente);
        fazenda.setAtivo(true);

        return fazendaRepository.save(fazenda);
    }

    public List<Fazenda> listarPorCliente(Integer clienteId) {
        clienteService.buscarPorId(clienteId);

        return fazendaRepository.findByClienteId(clienteId);
    }

    public Fazenda buscarPorId(Integer clienteId, Integer fazendaId) {

        return fazendaRepository.findByIdAndClienteId(fazendaId,clienteId)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,"Fazenda não encontrada para este cliente")
            );
    }

            
@Transactional
public Fazenda atualizar(Integer clienteId, Integer fazendaId, Fazenda dados) {

    // Busca a fazenda e garante que pertence ao cliente
    Fazenda fazenda = buscarPorId(clienteId, fazendaId);

    // Reutiliza as validações, ignorando o próprio ID
    // na verificação de IE duplicada
    validarFazenda(dados, fazendaId);

    fazenda.setNome(dados.getNome());
    fazenda.setTipoIe(dados.getTipoIe());
    fazenda.setInscricaoEstadual(dados.getInscricaoEstadual());

    fazenda.setCep(dados.getCep());
    fazenda.setEstado(dados.getEstado());
    fazenda.setCidade(dados.getCidade());
    fazenda.setBairro(dados.getBairro());
    fazenda.setLogradouro(dados.getLogradouro());
    fazenda.setNumero(dados.getNumero());
    fazenda.setComplemento(dados.getComplemento());
    fazenda.setObservacoes(dados.getObservacoes());

    //Ativo
    fazenda.setAtivo(dados.getAtivo());

    return fazendaRepository.save(fazenda);
}





    private void validarFazenda(Fazenda fazenda, Integer idExcluir) {

        if (fazenda.getNome() == null || fazenda.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da fazenda é obrigatório");
        }

        if (fazenda.getTipoIe() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indicador de IE é obrigatório");
        }

        normalizarEndereco(fazenda);
        validarInscricaoEstadual(fazenda, idExcluir);
    }


    

    private void validarInscricaoEstadual(Fazenda fazenda, Integer idExcluir) {

        if (fazenda.getTipoIe() == TipoIE.CONTRIBUINTE) {
            if (fazenda.getInscricaoEstadual() == null || fazenda.getInscricaoEstadual().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inscrição Estadual é obrigatória para contribuinte");
            }
            if (fazenda.getEstado() == null || fazenda.getEstado().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado é obrigatório para fazenda contribuinte");
            }

            String ie = fazenda.getInscricaoEstadual().replaceAll("[^A-Za-z0-9]", "").toUpperCase();

            fazenda.setInscricaoEstadual(ie);

            boolean ieDuplicada;

            if (idExcluir == null) {
                // Cadastro: verifica todas as fazendas
                ieDuplicada = fazendaRepository.existsByInscricaoEstadualAndEstadoIgnoreCase(ie, fazenda.getEstado());
            } else {
                // Atualização: ignora a própria fazenda
                ieDuplicada = fazendaRepository.existsByInscricaoEstadualAndEstadoIgnoreCaseAndIdNot(ie, fazenda.getEstado(), idExcluir);
            }
            if (ieDuplicada) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outra fazenda com esta IE neste estado");
            }
            return;
        }
        // ISENTO ou NAO_CONTRIBUINTE
        fazenda.setInscricaoEstadual(null);
    }

    private void normalizarEndereco(Fazenda fazenda) {
        if (fazenda.getEstado() != null && !fazenda.getEstado().isBlank()) {

            String estado = fazenda.getEstado().trim().toUpperCase();

            if (estado.length() != 2) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Estado deve ser informado pela sigla da UF");
            }
            fazenda.setEstado(estado);
        }

        if (fazenda.getCep() != null && !fazenda.getCep().isBlank()) {

            fazenda.setCep(fazenda.getCep().replaceAll("\\D", ""));

            if (fazenda.getCep().length() != 8) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP deve possuir 8 dígitos");
            }
        }
    }
}