
package com.moura.agrios.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.moura.agrios.models.Maquina;

@Repository
public interface MaquinaRepository extends JpaRepository<Maquina, Integer> {

    boolean existsByIdentificacaoIgnoreCase(String identificacao);

    boolean existsByIdentificacaoIgnoreCaseAndIdNot(String identificacao, Integer id);

    List<Maquina> findByAtivoTrue();
}