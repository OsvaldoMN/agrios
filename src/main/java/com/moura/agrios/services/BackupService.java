package com.moura.agrios.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BackupService {

    @Value("${backup.directory}") // pasta onde vai ser salvo os backup
    private String backupDirectory;

    @Value("${backup.mysql-dump-path}")
    private String mysqlDumpPath;

    @Value("${backup.mysql-path}")
    private String mysqlPath;

    @Value("${backup.database}") //nome do banco: agrios
    private String database;

    @Value("${backup.host}") //endereço do banco (localhost)
    private String host;

    @Value("${backup.port}") //posta do mysql: 3306
    private String port;

    @Value("${backup.username}") //usuario do banco: root
    private String username;

    @Value("${backup.password}") //senha banco
    private String password;


    public File gerarBackup() {

        try {

            Path pasta = Paths.get(backupDirectory);

            if (!Files.exists(pasta)) {
                Files.createDirectories(pasta);
            }

            String dataHora = LocalDateTime.now(ZoneId.of("America/Sao_Paulo")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            File arquivo = pasta.resolve("agrios_backup_" +dataHora +".sql").toFile();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    mysqlDumpPath,
                    "-h", host,
                    "-P", port,
                    "-u", username,

                    "--single-transaction",
                    "--routines",
                    "--triggers",
                    "--events",
                    "--set-gtid-purged=OFF",
                    database
                );

            
            processBuilder.environment().put("MYSQL_PWD", password); //variavel de ambiente para evitar colocar senha no código

            
            processBuilder.redirectOutput(arquivo); //contera todas as informações geradas pelo mysqldump

            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process processo = processBuilder.start();
            int resultado = processo.waitFor();

            if (resultado != 0) {
                throw new RuntimeException("Erro ao gerar backup do banco");
            }

            return arquivo;

        } catch (IOException | InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("Não foi possível gerar o backup", e);
        }
    }


    public void restaurarBackup(MultipartFile arquivo) {

        if (arquivo.isEmpty()) {
            throw new RuntimeException("Selecione um arquivo de backup");
        }

        String nomeArquivo = arquivo.getOriginalFilename();

        if (nomeArquivo == null || !nomeArquivo.toLowerCase().endsWith(".sql")) {

            throw new RuntimeException("Selecione um arquivo .sql válido");
        }

        Path arquivoTemporario = null;

        try {

            arquivoTemporario = Files.createTempFile("agrios_restore_", ".sql");

            // Neutraliza instruções GTID_PURGED caso o arquivo tenha sido gerado com GTID
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8));
                 var writer = Files.newBufferedWriter(arquivoTemporario, StandardCharsets.UTF_8)) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    if (linha.trim().startsWith("SET @@GLOBAL.GTID_PURGED")) {
                        writer.write("-- " + linha);
                    } else {
                        writer.write(linha);
                    }
                    writer.newLine();
                }
            }

            ProcessBuilder processBuilder = new ProcessBuilder(
                    mysqlPath,
                    "-h", host,
                    "-P", port,
                    "-u", username,
                    database
                );

            processBuilder.environment().put("MYSQL_PWD", password);

            /*
             * O MySQL lerá o arquivo SQL.
             */
            processBuilder.redirectInput(arquivoTemporario.toFile());

            Process processo = processBuilder.start();

            String errorOutput = new String(processo.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);

            int resultado = processo.waitFor();

            if (resultado != 0) {
                throw new RuntimeException("Erro ao restaurar backup: " + (errorOutput.isBlank() ? "Código " + resultado : errorOutput.trim()));
            }

        } catch (IOException | InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException("Não foi possível restaurar o backup: " + e.getMessage(), e);

        } finally {

            if (arquivoTemporario != null) {

                try {Files.deleteIfExists(arquivoTemporario);

                } catch (IOException ignored) {
                }
            }
        }
    }
}