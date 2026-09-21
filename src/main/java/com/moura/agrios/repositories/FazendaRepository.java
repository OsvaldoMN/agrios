package com.moura.agrios.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moura.agrios.models.Fazenda;

@Repository
public interface FazendaRepository extends JpaRepository<Fazenda, Integer> {

    List<Fazenda> findByClienteId(Integer clienteId);

    Optional<Fazenda> findByIdAndClienteId(Integer id, Integer clienteId);

    boolean existsByInscricaoEstadualAndEstadoIgnoreCase(String inscricaoEstadual, String estado);

}