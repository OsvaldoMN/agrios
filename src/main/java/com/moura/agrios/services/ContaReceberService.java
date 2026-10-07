package com.moura.agrios.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.moura.agrios.dtos.*;
import com.moura.agrios.enums.*;
import com.moura.agrios.models.*;
import com.moura.agrios.repositories.*;

@Service
public class ContaReceberService {

    private final ContaReceberRepository contaRepository;
    private final RecebimentoRepository recebimentoRepository;
    private final ClienteService clienteService;

    public ContaReceberService(ContaReceberRepository contaRepository,RecebimentoRepository recebimentoRepository,ClienteService clienteService) {

        this.contaRepository = contaRepository;
        this.recebimentoRepository = recebimentoRepository;
        this.clienteService = clienteService;
    }

    // ========================================
    // CONTA MANUAL
    // ========================================

    @Transactional
    public ContaReceberResponse cadastrarManual(CriarContaReceberRequest request) {

        Cliente cliente = clienteService.buscarPorId(request.clienteId());

        ContaReceber conta = new ContaReceber();

        conta.setCliente(cliente);
        conta.setOrdemServico(null);
        conta.setOrigem(OrigemContaReceber.MANUAL);
        conta.setDescricao(request.descricao().trim());
        conta.setValor(request.valor());
        conta.setDataVencimento(request.dataVencimento());
        conta.setObservacoes(request.observacoes());
        conta.setStatus(StatusContaReceber.ABERTA);

        ContaReceber salva = contaRepository.save(conta);
        atualizarStatus(salva);

        return mapearResposta(salva);
    }

    // ========================================
    // CONTA GERADA POR OS
    // ========================================

    @Transactional
    public void criarPorOrdemServico(OrdemServico os, LocalDate dataVencimento) {

        if (os.getStatus() != StatusOS.FINALIZADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A OS precisa estar finalizada");
        }

        if (contaRepository.existsByOrdemServicoId(os.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma conta a receber para esta OS");
        }

        ContaReceber conta = new ContaReceber();

        conta.setCliente(os.getCliente());
        conta.setOrdemServico(os);
        conta.setOrigem(OrigemContaReceber.ORDEM_SERVICO);
        conta.setStatus(StatusContaReceber.ABERTA);
        conta.setDescricao("Ordem de Serviço #" + os.getId());
        conta.setValor(os.getValorTotal());
        conta.setDataVencimento(dataVencimento);

        contaRepository.save(conta);
        atualizarStatus(conta);
    }

    // ========================================
    // REGISTRAR PAGAMENTO
    // ========================================

    @Transactional
    public ContaReceberResponse registrarRecebimento(Integer contaId, RegistrarRecebimentoRequest request) {

        ContaReceber conta = buscarConta(contaId);

        if (conta.getStatus() == StatusContaReceber.CANCELADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Não é possível receber uma conta cancelada");
        }

        if (conta.getStatus() == StatusContaReceber.QUITADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta conta já está quitada");
        }

        BigDecimal recebido = calcularValorRecebido(contaId);
        BigDecimal saldo = conta.getValor().subtract(recebido);

        if (request.valor().compareTo(saldo) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Pagamento não pode ser maior que o saldo da conta");
        }

        LocalDate dataPagamento = request.dataPagamento() != null ? request.dataPagamento() : hoje();

        if (dataPagamento.isAfter(hoje())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data de pagamento não pode ser futura");
        }

        Recebimento recebimento = new Recebimento();

        recebimento.setContaReceber(conta);
        recebimento.setValor(request.valor());
        recebimento.setFormaPagamento(request.formaPagamento());
        recebimento.setDataPagamento(dataPagamento);
        recebimento.setObservacoes(request.observacoes());

        recebimentoRepository.save(recebimento);
        atualizarStatus(conta);

        return mapearResposta(conta);
    }

    // ========================================
    // CANCELAR PELA OS
    // ========================================

    @Transactional
    public void cancelarPorOrdemServico(Integer ordemServicoId) {

        ContaReceber conta = contaRepository.findByOrdemServicoId(ordemServicoId).orElse(null);

        /*
         * OS aberta cancelada:
         * ainda não existe conta.
         */
        if (conta == null) {
            return;
        }

        conta.setStatus(StatusContaReceber.CANCELADA);
        contaRepository.save(conta);
    }

    // ========================================
    // BUSCAR
    // ========================================

    @Transactional
    public ContaReceberResponse buscarPorId(Integer id) {
        ContaReceber conta = buscarConta(id);
        atualizarStatus(conta);
        return mapearResposta(conta);
    }

    // ========================================
    // LISTAR
    // ========================================

    @Transactional
    public List<ContaReceberResponse> listarTodas() {

        List<ContaReceber> contas = contaRepository.findAll();

        for (ContaReceber conta : contas) {
            atualizarStatus(conta);
        }

        return contas.stream().map(this::mapearResposta).toList();
    }

    // ========================================
    // ATUALIZAR CONTAS ATRASADAS
    // ========================================

    @Transactional
    public void atualizarContasAtrasadas() {

        List<ContaReceber> contas = contaRepository.findAll();

        for (ContaReceber conta : contas) {
            atualizarStatus(conta);
        }
    }

    // ========================================
    // STATUS
    // ========================================

    private void atualizarStatus(ContaReceber conta) {

        /*
         * Cancelada nunca muda automaticamente.
         */
        if (conta.getStatus() == StatusContaReceber.CANCELADA) {
            return;
        }

        BigDecimal recebido = calcularValorRecebido(conta.getId());

        /*
         * Pagou tudo.
         */
        if (recebido.compareTo(conta.getValor()) >= 0) {

            conta.setStatus(StatusContaReceber.QUITADA);
            contaRepository.save(conta);
            return;
        }

        /*
         * Chegou ao dia do vencimento
         * e ainda existe saldo.
         */
        if (!conta.getDataVencimento().isAfter(hoje())) {

            conta.setStatus(StatusContaReceber.ATRASADA);
            contaRepository.save(conta);
            return;
        }

        /*
         * Pagou alguma coisa,
         * mas ainda possui saldo.
         */
        if (recebido.compareTo(BigDecimal.ZERO) > 0) {
            conta.setStatus(StatusContaReceber.PARCIAL);

        } else {
            conta.setStatus(StatusContaReceber.ABERTA);
        }
        contaRepository.save(conta);
    }

    // ========================================
    // VALOR RECEBIDO
    // ========================================

    private BigDecimal calcularValorRecebido(Integer contaId) {

        return recebimentoRepository.findByContaReceberIdOrderByDataPagamentoAsc(contaId)
            .stream()
            .map(Recebimento::getValor)
            .reduce(
                BigDecimal.ZERO,
                BigDecimal::add
            );
    }

    // ========================================
    // BUSCAR ENTIDADE
    // ========================================

    private ContaReceber buscarConta(Integer id) {

        return contaRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Conta a receber não encontrada"));
    }

    // ========================================
    // DATA ATUAL
    // ========================================

    private LocalDate hoje() {
        return LocalDate.now(ZoneId.of("America/Sao_Paulo"));
    }

    // ========================================
    // RESPONSE
    // ========================================

    private ContaReceberResponse mapearResposta(ContaReceber conta) {

        Cliente cliente = conta.getCliente();

        String clienteNome = cliente.getNome() != null ? cliente.getNome() : cliente.getRazaoSocial();

        Integer ordemServicoId = conta.getOrdemServico() != null ? conta.getOrdemServico().getId() : null;

        List<Recebimento> recebimentos = recebimentoRepository.findByContaReceberIdOrderByDataPagamentoAsc(conta.getId());

        BigDecimal valorRecebido = recebimentos
                                .stream()
                                .map(Recebimento::getValor)
                                .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                                );

        BigDecimal saldo = conta.getValor().subtract(valorRecebido);

        List<RecebimentoResponse> pagamentos = recebimentos
                .stream()
                .map(r -> new RecebimentoResponse(
                    r.getId(),
                    r.getValor(),
                    r.getFormaPagamento(),
                    r.getDataPagamento(),
                    r.getObservacoes(),
                    r.getCriadoEm()
                ))
                .toList();

        return new ContaReceberResponse(

            conta.getId(),

            cliente.getId(),
            clienteNome,

            ordemServicoId,

            conta.getOrigem(),
            conta.getStatus(),

            conta.getDescricao(),

            conta.getValor(),
            valorRecebido,
            saldo,

            conta.getDataEmissao(),
            conta.getDataVencimento(),

            pagamentos,

            conta.getObservacoes(),

            conta.getCriadoEm()
        );
    }
}