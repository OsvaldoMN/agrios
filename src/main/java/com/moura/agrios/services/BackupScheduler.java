package com.moura.agrios.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BackupScheduler {

    private final BackupService backupService;

    public BackupScheduler(BackupService backupService) {
        this.backupService = backupService;
    }

    @Scheduled(cron = "${backup.cron}",zone = "America/Sao_Paulo")

    public void executarBackupAutomatico() {
        backupService.gerarBackup();
    }
}