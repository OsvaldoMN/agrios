package com.moura.agrios.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moura.agrios.models.Recebimento;

@Repository
public interface RecebimentoRepository extends JpaRepository<Recebimento, Integer> {

    List<Recebimento> findByContaReceberIdOrderByDataPagamentoAsc(Integer contaReceberId);
}