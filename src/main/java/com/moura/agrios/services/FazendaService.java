package com.moura.agrios.services;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.moura.agrios.enums.TipoIE;
import com.moura.agrios.models.Cliente;
import com.moura.agrios.models.Fazenda;
import com.moura.agrios.repositories.FazendaRepository;

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

        validarFazenda(fazenda);

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

    private void validarFazenda(Fazenda fazenda) {

        if (fazenda.getNome() == null || fazenda.getNome().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da fazenda é obrigatório");
        }

        if (fazenda.getTipoIe() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Indicador de IE é obrigatório");
        }

        normalizarEndereco(fazenda);
        validarInscricaoEstadual(fazenda);
    }

    private void validarInscricaoEstadual(Fazenda fazenda) {

        if (fazenda.getTipoIe() == TipoIE.CONTRIBUINTE) {

            if (fazenda.getInscricaoEstadual() == null || fazenda.getInscricaoEstadual().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inscrição Estadual é obrigatória para contribuinte");
            }

            if (fazenda.getEstado() == null || fazenda.getEstado().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado é obrigatório para fazenda contribuinte");
            }

            String ie = fazenda.getInscricaoEstadual().replaceAll("[^A-Za-z0-9]", "").toUpperCase();

            fazenda.setInscricaoEstadual(ie);

            //Verifica se tem 2 IE iguais dentro do mesmo estado
            if (fazendaRepository.existsByInscricaoEstadualAndEstadoIgnoreCase(ie,fazenda.getEstado())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma fazenda cadastrada com esta IE neste estado");
            }
            return;
        }

        //Caso tipoIE != Contribuinte
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