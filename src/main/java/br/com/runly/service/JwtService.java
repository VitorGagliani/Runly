package br.com.runly.service;

import br.com.runly.model.Usuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private final ObjectMapper objectMapper;

    @Value("${api.security.token.secret:runly-secret-key-change-me}")
    private String secret;

    @Value("${api.security.token.expiration-ms:86400000}")
    private Long expirationMs;

    public JwtService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String gerarToken(Usuario usuario) {
        try {
            long agora = Instant.now().getEpochSecond();
            long expiraEm = agora + (expirationMs / 1000);

            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("sub", usuario.getEmail());
            payload.put("id", usuario.getId());
            payload.put("nome", usuario.getNome());
            payload.put("iat", agora);
            payload.put("exp", expiraEm);

            String headerBase64 = encodeJson(header);
            String payloadBase64 = encodeJson(payload);
            String assinatura = assinar(headerBase64 + "." + payloadBase64);

            return headerBase64 + "." + payloadBase64 + "." + assinatura;
        } catch (Exception exception) {
            throw new IllegalStateException("Erro ao gerar token JWT", exception);
        }
    }

    public String validarTokenEObterEmail(String token) {
        try {
            String[] partes = token.split("\\.");
            if (partes.length != 3) {
                return null;
            }

            String conteudoAssinado = partes[0] + "." + partes[1];
            String assinaturaEsperada = assinar(conteudoAssinado);
            if (!assinaturaEsperada.equals(partes[2])) {
                return null;
            }

            Map<String, Object> payload = decodePayload(partes[1]);
            Number exp = (Number) payload.get("exp");
            if (exp == null || Instant.now().getEpochSecond() > exp.longValue()) {
                return null;
            }

            return (String) payload.get("sub");
        } catch (Exception exception) {
            return null;
        }
    }

    private String encodeJson(Map<String, Object> json) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(json);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private Map<String, Object> decodePayload(String payloadBase64) throws Exception {
        byte[] bytes = Base64.getUrlDecoder().decode(payloadBase64);
        return objectMapper.readValue(bytes, new TypeReference<>() {});
    }

    private String assinar(String conteudo) throws Exception {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(key);
        byte[] assinatura = mac.doFinal(conteudo.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(assinatura);
    }
}
