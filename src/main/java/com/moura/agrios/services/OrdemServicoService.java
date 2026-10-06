
package com.moura.agrios.services;

import java.math.BigDecimal;
import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.dtos.*;
import com.moura.agrios.enums.StatusOS;
import com.moura.agrios.models.*;
import com.moura.agrios.repositories.OrdemServicoRepository;

import jakarta.validation.Valid;

@Service
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final ClienteService clienteService;
    private final FazendaService fazendaService;
    private final ServicoService servicoService;
    private final ProdutoService produtoService;
    private final MaquinaService maquinaService;

    public OrdemServicoService(
            OrdemServicoRepository osRepository,
            ClienteService clienteService,
            FazendaService fazendaService,
            ServicoService servicoService,
            ProdutoService produtoService,
            MaquinaService maquinaService) {

        this.osRepository = osRepository;
        this.clienteService = clienteService;
        this.fazendaService = fazendaService;
        this.servicoService = servicoService;
        this.produtoService = produtoService;
        this.maquinaService = maquinaService;
    }

    // ========================================
    // CADASTRAR
    // ========================================

    @Transactional
    public OrdemServicoResponse cadastrar(
            CadastrarOrdemServicoRequest request) {

        OrdemServico os = new OrdemServico();

        preencherDados(os, request);

        os.setStatus(StatusOS.ABERTA);

        adicionarMaquinas(os, request.maquinas());

        return mapearResposta(osRepository.saveAndFlush(os));
    }

    // ========================================
    // EDITAR
    // ========================================

    @Transactional
public OrdemServicoResponse atualizar(Integer id, CadastrarOrdemServicoRequest request) {

    OrdemServico os = buscarOrdem(id);

    if (os.getStatus() != StatusOS.ABERTA) {
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Somente OS abertas podem ser editadas");
    }
    preencherDados(os, request);
    os.getMaquinas().clear();
    osRepository.flush();
    adicionarMaquinas(os, request.maquinas());

    return mapearResposta(
        osRepository.saveAndFlush(os)
    );
}

    // ========================================
    // FINALIZAR
    // ========================================

    @Transactional
    public OrdemServicoResponse finalizar(Integer id) {

        OrdemServico os = buscarOrdem(id);

        if (os.getStatus() != StatusOS.ABERTA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Somente OS abertas podem ser finalizadas");
        }

        if (os.getDataFim() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Preencha a data de fim antes de finalizar a OS");
        }

        os.setStatus(StatusOS.FINALIZADA);

        return mapearResposta(osRepository.save(os));
    }

    // ========================================
    // CANCELAR
    // ========================================

    @Transactional
    public OrdemServicoResponse cancelar(Integer id) {

        OrdemServico os = buscarOrdem(id);

        if (os.getStatus() == StatusOS.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta OS já está cancelada");
        }

        os.setStatus(StatusOS.CANCELADA);

        return mapearResposta(osRepository.save(os));
    }

    // ========================================
    // CONSULTAR
    // ========================================

    @Transactional(readOnly = true)
    public OrdemServicoResponse buscarPorId(Integer id) {
        return mapearResposta(buscarOrdem(id));
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoResponse> listarTodas() {

        return osRepository.findAll()
            .stream()
            .map(this::mapearResposta)
            .toList();
    }

    private OrdemServico buscarOrdem(Integer id) {
        return osRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Ordem de Serviço não encontrada"));
    }

    // ========================================
    // PREENCHER DADOS DA OS
    // ========================================

    private void preencherDados(OrdemServico os, CadastrarOrdemServicoRequest request) {

        // Cliente
        Cliente cliente = clienteService.buscarPorId(request.clienteId());
        validarAtivo(cliente.getAtivo(), "Cliente");

        // Fazenda pertencente ao cliente
        Fazenda fazenda = fazendaService.buscarPorId(request.clienteId(), request.fazendaId());
        validarAtivo(fazenda.getAtivo(), "Fazenda");

        // Serviço
        Servico servico = servicoService.buscarPorId(request.servicoId());
        validarAtivo(servico.getAtivo(), "Serviço");

        // Produto opcional
        Produto produto = null;

        if (request.produtoId() != null) {
            produto = produtoService.buscarPorId(request.produtoId());
            validarAtivo(produto.getAtivo(), "Produto");
        }

        // Validação das datas
        if (request.dataInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data de início é obrigatória");
        }

        if (request.dataFim() != null && request.dataFim().isBefore(request.dataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data de fim não pode ser anterior à data de início");
        }

        // Preenche os campos
        os.setCliente(cliente);
        os.setFazenda(fazenda);
        os.setServico(servico);
        os.setProduto(produto);

        os.setDataInicio(request.dataInicio());
        os.setDataFim(request.dataFim());

        os.setHectares(request.hectares());
        os.setObservacoes(request.observacoes());
    }

    // ========================================
    // ADICIONAR MÁQUINAS E CALCULAR O TOTAL
    // ========================================

    private void adicionarMaquinas(OrdemServico os, List<MaquinaOSRequest> maquinas) {

        if (maquinas == null || maquinas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe pelo menos uma máquina");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (MaquinaOSRequest item : maquinas) {

            if (item == null || item.maquinaId() == null || item.valor() == null || item.valor().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe uma máquina e um valor maior que zero");
            }

            // Verifica se a máquina já foi adicionada.
            for (MaquinaOS existente : os.getMaquinas()) {
                if (existente.getMaquina().getId().equals(item.maquinaId())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máquina informada mais de uma vez");
                }
            }

            // Busca a máquina cadastrada.
            Maquina maquina = maquinaService.buscarPorId(item.maquinaId());
            validarAtivo(maquina.getAtivo(), "Máquina");

            // Cria o vínculo entre a máquina e a OS.
            MaquinaOS maquinaOS = new MaquinaOS();

            maquinaOS.setMaquina(maquina);
            maquinaOS.setValor(item.valor());

            os.adicionarMaquina(maquinaOS);

            // Soma o valor desta máquina.
            total = total.add(item.valor());
        }

        if (total.compareTo(new BigDecimal("9999999999999.99")) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor total ultrapassa o limite permitido");
        }

        os.setValorTotal(total);
    }

    // ========================================
    // VALIDAR CADASTRO ATIVO
    // ========================================

    private void validarAtivo(Boolean ativo, String entidade) {

        if (!Boolean.TRUE.equals(ativo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, entidade + " está desativado(a)");
        }
    }

    // ========================================
    // CONVERTER OS PARA DTO DE RESPOSTA
    // ========================================

    private OrdemServicoResponse mapearResposta(OrdemServico os) {

        Cliente cliente = os.getCliente();
        Fazenda fazenda = os.getFazenda();
        Servico servico = os.getServico();
        Produto produto = os.getProduto();

        String clienteNome = cliente.getNome() != null ? cliente.getNome() : cliente.getRazaoSocial();

        List<MaquinaOSResponse> maquinas = os.getMaquinas()
            .stream()
            .map(item -> new MaquinaOSResponse(
                item.getId(),
                item.getMaquina().getId(),
                item.getMaquina().getNome(),
                item.getMaquina().getIdentificacao(),
                item.getValor()
            ))
            .toList();

        return new OrdemServicoResponse(
            os.getId(),

            cliente.getId(),
            clienteNome,

            fazenda.getId(),
            fazenda.getNome(),

            servico.getId(),
            servico.getNome(),

            produto != null ? produto.getId() : null,
            produto != null ? produto.getNome() : null,

            os.getDataInicio(),
            os.getDataFim(),
            os.getCriadoEm(),

            os.getStatus(),
            os.getHectares(),
            maquinas,
            os.getValorTotal(),
            os.getObservacoes()
        );
    }

    public static @Nullable OrdemServicoResponse atualizar(Integer id, OrdemServicoResponse request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'atualizar'");
    }
}