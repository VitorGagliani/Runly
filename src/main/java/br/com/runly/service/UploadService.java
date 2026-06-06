package br.com.runly.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

    @Value("${runly.upload.dir}")
    private String uploadDir;

    public String salvarFotoGrupo(Long grupoId, MultipartFile arquivo) {
        try {
            if (arquivo.isEmpty()) {
                throw new RuntimeException("Arquivo vazio.");
            }

            String contentType = arquivo.getContentType();

            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("O arquivo precisa ser uma imagem.");
            }

            String nomeOriginal = arquivo.getOriginalFilename();

            if (nomeOriginal == null) {
                throw new RuntimeException("Nome do arquivo inválido.");
            }

            String extensao = pegarExtensao(nomeOriginal);

            String nomeArquivo = "grupo-" + grupoId + "-" + System.currentTimeMillis() + extensao;

            Path pastaUploads = Paths.get(uploadDir, "grupos");

            Files.createDirectories(pastaUploads);

            Path caminhoArquivo = pastaUploads.resolve(nomeArquivo);

            Files.copy(arquivo.getInputStream(), caminhoArquivo);

            return "/uploads/grupos/" + nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo.", e);
        }
    }

    private String pegarExtensao(String nomeArquivo) {
        int ultimoPonto = nomeArquivo.lastIndexOf(".");

        if (ultimoPonto == -1) {
            return "";
        }

        return nomeArquivo.substring(ultimoPonto);
    }
}