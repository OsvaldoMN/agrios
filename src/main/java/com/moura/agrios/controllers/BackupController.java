package com.moura.agrios.controllers;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.moura.agrios.services.BackupService;

@RestController
@RequestMapping("/backup")
public class BackupController {

    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }


    @GetMapping("/gerar")
    public ResponseEntity<Resource> gerarBackup() {

        File arquivo = backupService.gerarBackup();

        Resource resource = new FileSystemResource(arquivo);

        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" +
                arquivo.getName() +
                "\""
            )
            .contentType(
                MediaType.APPLICATION_OCTET_STREAM
            )
            .body(resource);
    }


    @PostMapping("/restaurar")
    public ResponseEntity<String> restaurarBackup(@RequestParam("arquivo") MultipartFile arquivo) {

        backupService.restaurarBackup(arquivo);
        return ResponseEntity.ok("Backup restaurado com sucesso");
    }
}