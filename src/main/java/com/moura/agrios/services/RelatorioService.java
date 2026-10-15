package com.moura.agrios.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moura.agrios.dtos.*;
import com.moura.agrios.enums.StatusContaReceber;
import com.moura.agrios.enums.StatusOS;
import com.moura.agrios.models.*;
import com.moura.agrios.repositories.ContaReceberRepository;
import com.moura.agrios.repositories.OrdemServicoRepository;
import com.moura.agrios.repositories.RecebimentoRepository;

import jakarta.persistence.criteria.Predicate;

@Service
public class RelatorioService {

    private final OrdemServicoRepository ordemServicoRepository;
    private final ContaReceberRepository contaReceberRepository;
    private final RecebimentoRepository recebimentoRepository;

    public RelatorioService(
        OrdemServicoRepository ordemServicoRepository,
        ContaReceberRepository contaReceberRepository,
        RecebimentoRepository recebimentoRepository
    ) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.contaReceberRepository = contaReceberRepository;
        this.recebimentoRepository = recebimentoRepository;
    }

    // =========================================================================
    // 1. RELATÓRIO DE ORDENS DE SERVIÇO
    // =========================================================================
    @Transactional(readOnly = true)
    public RelatorioOSResponseDTO gerarRelatorioOS(
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer clienteId,
        Integer fazendaId,
        Integer servicoId,
        Integer produtoId,
        StatusOS status
    ) {
        Specification<OrdemServico> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), dataFim));
            }
            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }
            if (fazendaId != null) {
                predicates.add(cb.equal(root.get("fazenda").get("id"), fazendaId));
            }
            if (servicoId != null) {
                predicates.add(cb.equal(root.get("servico").get("id"), servicoId));
            }
            if (produtoId != null) {
                predicates.add(cb.equal(root.get("produto").get("id"), produtoId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "dataInicio", "id");
        List<OrdemServico> lista = ordemServicoRepository.findAll(spec, sort);

        List<RelatorioOSItemDTO> itens = lista.stream().map(os -> {
            Cliente c = os.getCliente();
            String clienteNome = (c.getNome() != null && !c.getNome().isBlank()) ? c.getNome() : c.getRazaoSocial();
            String fazendaNome = os.getFazenda() != null ? os.getFazenda().getNome() : "-";
            String servicoNome = os.getServico() != null ? os.getServico().getNome() : "-";
            String produtoNome = os.getProduto() != null ? os.getProduto().getNome() : "-";
            BigDecimal hectares = os.getHectares() != null ? os.getHectares() : BigDecimal.ZERO;
            BigDecimal valorTotal = os.getValorTotal() != null ? os.getValorTotal() : BigDecimal.ZERO;

            return new RelatorioOSItemDTO(
                os.getId(),
                os.getDataInicio(),
                os.getDataFim(),
                clienteNome,
                fazendaNome,
                servicoNome,
                produtoNome,
                hectares,
                os.getStatus(),
                valorTotal
            );
        }).toList();

        long totalOrdens = itens.size();

        // Ordens canceladas não somam hectares nem valores financeiros
        BigDecimal totalHectares = itens.stream()
            .filter(i -> i.status() != StatusOS.CANCELADA)
            .map(RelatorioOSItemDTO::hectares)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorTotal = itens.stream()
            .filter(i -> i.status() != StatusOS.CANCELADA)
            .map(RelatorioOSItemDTO::valorTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioOSResponseDTO(itens, totalOrdens, totalHectares, valorTotal);
    }

    // =========================================================================
    // 2. RELATÓRIO DE CONTAS A RECEBER
    // =========================================================================
    @Transactional(readOnly = true)
    public RelatorioContaReceberResponseDTO gerarRelatorioContasReceber(
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer clienteId,
        StatusContaReceber status
    ) {
        Specification<ContaReceber> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataVencimento"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataVencimento"), dataFim));
            }
            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.ASC, "dataVencimento", "id");
        List<ContaReceber> lista = contaReceberRepository.findAll(spec, sort);

        List<RelatorioContaReceberItemDTO> itens = lista.stream().map(conta -> {
            Cliente c = conta.getCliente();
            String clienteNome = (c.getNome() != null && !c.getNome().isBlank()) ? c.getNome() : c.getRazaoSocial();
            Integer osId = conta.getOrdemServico() != null ? conta.getOrdemServico().getId() : null;

            BigDecimal valorOriginal = conta.getValor() != null ? conta.getValor() : BigDecimal.ZERO;
            BigDecimal valorDesconto = conta.getValorDesconto() != null ? conta.getValorDesconto() : BigDecimal.ZERO;
            BigDecimal valorFinal = valorOriginal.subtract(valorDesconto);

            List<Recebimento> recebimentos = recebimentoRepository.findByContaReceberIdOrderByDataPagamentoAsc(conta.getId());
            BigDecimal valorRecebido = recebimentos.stream()
                .map(Recebimento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal saldo = valorFinal.subtract(valorRecebido);
            if (saldo.compareTo(BigDecimal.ZERO) < 0) {
                saldo = BigDecimal.ZERO;
            }

            return new RelatorioContaReceberItemDTO(
                conta.getId(),
                clienteNome,
                conta.getOrigem(),
                osId,
                conta.getDescricao(),
                conta.getDataEmissao(),
                conta.getDataVencimento(),
                conta.getStatus(),
                valorOriginal,
                valorDesconto,
                valorFinal,
                valorRecebido,
                saldo
            );
        }).toList();

        long totalContas = itens.size();

        // Contas canceladas não são somadas em valores a receber/saldo pendente
        BigDecimal valorOriginalTotal = itens.stream()
            .filter(i -> i.status() != StatusContaReceber.CANCELADA)
            .map(RelatorioContaReceberItemDTO::valorOriginal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDescontos = itens.stream()
            .filter(i -> i.status() != StatusContaReceber.CANCELADA)
            .map(RelatorioContaReceberItemDTO::valorDesconto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valorFinalTotal = itens.stream()
            .filter(i -> i.status() != StatusContaReceber.CANCELADA)
            .map(RelatorioContaReceberItemDTO::valorFinal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalRecebido = itens.stream()
            .map(RelatorioContaReceberItemDTO::valorRecebido)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoPendenteTotal = itens.stream()
            .filter(i -> i.status() != StatusContaReceber.CANCELADA)
            .map(RelatorioContaReceberItemDTO::saldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioContaReceberResponseDTO(
            itens,
            totalContas,
            valorOriginalTotal,
            totalDescontos,
            valorFinalTotal,
            totalRecebido,
            saldoPendenteTotal
        );
    }

    // =========================================================================
    // 3. RELATÓRIO DE TEMPO DE TRABALHO POR FAZENDA
    // =========================================================================
    @Transactional(readOnly = true)
    public RelatorioTrabalhoFazendaResponseDTO gerarRelatorioTrabalhoFazenda(
        LocalDate dataInicio,
        LocalDate dataFim,
        Integer clienteId,
        Integer fazendaId
    ) {
        Specification<OrdemServico> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataInicio"), dataFim));
            }
            if (clienteId != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), clienteId));
            }
            if (fazendaId != null) {
                predicates.add(cb.equal(root.get("fazenda").get("id"), fazendaId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.ASC, "fazenda.nome", "dataInicio");
        List<OrdemServico> lista = ordemServicoRepository.findAll(spec, sort);

        LocalDate hoje = LocalDate.now(ZoneId.of("America/Sao_Paulo"));

        List<RelatorioTrabalhoFazendaItemDTO> itens = lista.stream().map(os -> {
            Cliente c = os.getCliente();
            String clienteNome = (c.getNome() != null && !c.getNome().isBlank()) ? c.getNome() : c.getRazaoSocial();
            String fazendaNome = os.getFazenda() != null ? os.getFazenda().getNome() : "-";
            String servicoNome = os.getServico() != null ? os.getServico().getNome() : "-";

            // Contagem inclusiva: dataFim - dataInicio + 1 dia
            LocalDate dataTermino = os.getDataFim() != null ? os.getDataFim() : hoje;
            long dias = Math.max(1, ChronoUnit.DAYS.between(os.getDataInicio(), dataTermino) + 1);

            BigDecimal hectares = os.getHectares() != null ? os.getHectares() : BigDecimal.ZERO;

            return new RelatorioTrabalhoFazendaItemDTO(
                clienteNome,
                fazendaNome,
                os.getId(),
                servicoNome,
                os.getDataInicio(),
                os.getDataFim(),
                dias,
                hectares,
                os.getStatus()
            );
        }).toList();

        // Agrupamento por Fazenda (apenas OS válidas / não canceladas para os totais da fazenda)
        Map<String, List<RelatorioTrabalhoFazendaItemDTO>> itensPorFazenda = itens.stream()
            .collect(Collectors.groupingBy(i -> i.fazendaNome() + " (" + i.clienteNome() + ")", LinkedHashMap::new, Collectors.toList()));

        List<ResumoFazendaDTO> resumosPorFazenda = new ArrayList<>();
        for (Map.Entry<String, List<RelatorioTrabalhoFazendaItemDTO>> entry : itensPorFazenda.entrySet()) {
            List<RelatorioTrabalhoFazendaItemDTO> osList = entry.getValue();
            String fazenda = osList.get(0).fazendaNome();
            String cliente = osList.get(0).clienteNome();

            long qtd = osList.size();

            long diasFazenda = osList.stream()
                .filter(i -> i.status() != StatusOS.CANCELADA)
                .mapToLong(RelatorioTrabalhoFazendaItemDTO::diasTrabalho)
                .sum();

            BigDecimal hectaresFazenda = osList.stream()
                .filter(i -> i.status() != StatusOS.CANCELADA)
                .map(RelatorioTrabalhoFazendaItemDTO::hectares)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            resumosPorFazenda.add(new ResumoFazendaDTO(fazenda, cliente, qtd, diasFazenda, hectaresFazenda));
        }

        long totalOrdens = itens.size();

        long totalDias = itens.stream()
            .filter(i -> i.status() != StatusOS.CANCELADA)
            .mapToLong(RelatorioTrabalhoFazendaItemDTO::diasTrabalho)
            .sum();

        BigDecimal totalHectares = itens.stream()
            .filter(i -> i.status() != StatusOS.CANCELADA)
            .map(RelatorioTrabalhoFazendaItemDTO::hectares)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RelatorioTrabalhoFazendaResponseDTO(
            itens,
            resumosPorFazenda,
            totalOrdens,
            totalDias,
            totalHectares
        );
    }
}
