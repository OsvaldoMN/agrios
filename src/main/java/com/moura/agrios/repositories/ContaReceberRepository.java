package com.moura.agrios.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moura.agrios.models.ContaReceber;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Integer> {

    boolean existsByOrdemServicoId(Integer ordemServicoId);

    Optional<ContaReceber> findByOrdemServicoId(Integer ordemServicoId);
}