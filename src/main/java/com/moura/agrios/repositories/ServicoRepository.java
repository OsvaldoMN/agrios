
package com.moura.agrios.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moura.agrios.models.Servico;

@Repository
public interface ServicoRepository extends JpaRepository<Servico, Integer> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(
        String nome,
        Integer id
    );

    List<Servico> findByAtivoTrue();
}