package com.veterinaria.dogtor.seguridad;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JWT (HS256) hecho a mano con javax.crypto.Mac para no depender de una
 * librería externa: este entorno compila con Maven offline y una dependencia
 * nueva rompería la build si no está en la caché local.
 */
@Service
public class JwtService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();
    private static final String HEADER_JSON = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";

    private final String secreto;
    private final long expiracionSegundos;

    public JwtService(@Value("${dogtor.jwt.secret}") String secreto,
                       @Value("${dogtor.jwt.expiracion-horas}") long expiracionHoras) {
        this.secreto = secreto;
        this.expiracionSegundos = expiracionHoras * 3600;
    }

    public record Claims(String correo, String rol) {
    }

    public String generarToken(String correo, String rol) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("correo", correo);
        payload.put("rol", rol);
        payload.put("exp", Instant.now().getEpochSecond() + expiracionSegundos);

        String headerB64 = encode(HEADER_JSON);
        String payloadB64 = encode(toJson(payload));
        String firma = firmar(headerB64 + "." + payloadB64);
        return headerB64 + "." + payloadB64 + "." + firma;
    }

    public Claims validar(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            return null;
        }
        String firmaEsperada = firmar(partes[0] + "." + partes[1]);
        if (!firmaEsperada.equals(partes[2])) {
            return null;
        }
        try {
            Map<?, ?> payload = MAPPER.readValue(DECODER.decode(partes[1]), Map.class);
            long exp = ((Number) payload.get("exp")).longValue();
            if (Instant.now().getEpochSecond() > exp) {
                return null;
            }
            return new Claims((String) payload.get("correo"), (String) payload.get("rol"));
        } catch (Exception e) {
            return null;
        }
    }

    private String firmar(String datos) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return ENCODER.encodeToString(mac.doFinal(datos.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("No se pudo firmar el JWT", e);
        }
    }

    private String encode(String json) {
        return ENCODER.encodeToString(json.getBytes(StandardCharsets.UTF_8));
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return MAPPER.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo serializar el payload del JWT", e);
        }
    }
}
