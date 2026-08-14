package br.com.runly.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class UploadService {

    @Value("${runly.upload.dir:uploads}")
    private String uploadDir;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.url:}")
    private String cloudinaryUrl;

    private final Cloudinary cloudinary;

    public UploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String salvarFotoGrupo(Long grupoId, MultipartFile arquivo) {
        validarImagem(arquivo);

        if (temCloudinaryConfigurado()) {
            return uploadParaCloudinary(arquivo, "runly/grupos", "grupo-" + grupoId + "-" + System.currentTimeMillis());
        }

        return salvarLocalmente(arquivo, "grupos", "grupo-" + grupoId + "-" + System.currentTimeMillis());
    }

    public String salvarFotoPerfil(Long usuarioId, MultipartFile arquivo) {
        validarImagem(arquivo);

        if (temCloudinaryConfigurado()) {
            return uploadParaCloudinary(arquivo, "runly/usuarios", "usuario-" + usuarioId + "-" + System.currentTimeMillis());
        }

        return salvarLocalmente(arquivo, "usuarios", "usuario-" + usuarioId + "-" + System.currentTimeMillis());
    }

    public String salvarFotoPost(MultipartFile arquivo) {
        validarImagem(arquivo);

        if (temCloudinaryConfigurado()) {
            return uploadParaCloudinary(arquivo, "runly/posts", "post-" + System.currentTimeMillis());
        }

        return salvarLocalmente(arquivo, "posts", "post-" + System.currentTimeMillis());
    }

    private String uploadParaCloudinary(MultipartFile arquivo, String pasta, String publicId) {
        try {
            Map<?, ?> resultado = cloudinary.uploader().upload(arquivo.getBytes(), ObjectUtils.asMap(
                    "folder", pasta,
                    "public_id", publicId,
                    "overwrite", true,
                    "resource_type", "auto"
            ));

            Object secureUrl = resultado.get("secure_url");
            if (secureUrl != null) {
                return secureUrl.toString();
            }

            Object url = resultado.get("url");
            return url != null ? url.toString() : "";
        } catch (IOException e) {
            throw new RuntimeException("Erro ao fazer upload da imagem para o Cloudinary: " + e.getMessage(), e);
        }
    }

    private String salvarLocalmente(MultipartFile arquivo, String subpasta, String prefixoNome) {
        try {
            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = pegarExtensao(nomeOriginal != null ? nomeOriginal : "foto.jpg");
            String nomeArquivo = prefixoNome + extensao;

            Path pastaUploads = Paths.get(uploadDir, subpasta);
            Files.createDirectories(pastaUploads);

            Path caminhoArquivo = pastaUploads.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoArquivo);

            return "/uploads/" + subpasta + "/" + nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo localmente.", e);
        }
    }

    private void validarImagem(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo de imagem vazio.");
        }

        String contentType = arquivo.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("O arquivo enviado precisa ser uma imagem válida.");
        }
    }

    private boolean temCloudinaryConfigurado() {
        return (cloudName != null && !cloudName.isBlank()) || (cloudinaryUrl != null && !cloudinaryUrl.isBlank());
    }

    private String pegarExtensao(String nomeArquivo) {
        int ultimoPonto = nomeArquivo.lastIndexOf(".");
        if (ultimoPonto == -1) {
            return ".jpg";
        }
        return nomeArquivo.substring(ultimoPonto);
    }
}