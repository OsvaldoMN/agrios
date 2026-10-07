package com.moura.agrios.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

// classe dedicada apenas para atualizar as contas vencidas

@Component
public class ContaReceberScheduler {

    private final ContaReceberService contaReceberService;

    public ContaReceberScheduler(ContaReceberService contaReceberService) {
        this.contaReceberService = contaReceberService;
    }


    // Todos os dias às 00:05

    @Scheduled(cron = "0 5 0 * * *", zone = "America/Sao_Paulo")
    public void atualizarContasAtrasadas() {
        contaReceberService.atualizarContasAtrasadas();
    }
}