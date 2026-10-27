package com.example.hppmw.service;

import com.example.hppmw.model.AccessTokenRequest;
import com.example.hppmw.model.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;

@Service
public class GlobalPayAuthService {

    private static final Logger log = LoggerFactory.getLogger(GlobalPayAuthService.class);

    private static final DateTimeFormatter NONCE_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
            .withZone(ZoneOffset.UTC);

    private final RestClient restClient;
    private final String appId;
    private final String appKey;
    private final String endpointUrl;
    private final String gpVersion;
    private final Clock clock;

    public GlobalPayAuthService(
            RestClient restClient,
            @Value("${app_id}") String appId,
            @Value("${app_key}") String appKey,
            @Value("${globalpay.base.url:https://apis.sandbox.globalpay.com}") String baseUrl,
            @Value("${globalpay.ucp.accesstoken.url:/ucp/accesstoken}") String accessTokenUrl,
            @Value("${globalpay.ucp.version:2021-03-22}") String gpVersion,
            Clock clock) {
        this.restClient = restClient;
        this.appId = appId;
        this.appKey = appKey;
        this.endpointUrl = baseUrl + accessTokenUrl;
        this.gpVersion = gpVersion;
        this.clock = clock;
    }

    public ResponseEntity<AccessTokenResponse> requestAccessToken() {
        String nonce = generateNonce();
        String secret = calculateSecret(nonce, appKey);

        AccessTokenRequest requestPayload = new AccessTokenRequest(appId, nonce, secret);
        log.info("Requesting access token for app_id={} with nonce={}", appId, nonce);

        return restClient.post()
                .uri(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-GP-Version", gpVersion)
                .body(requestPayload)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), (request, response) -> {
                    String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.warn("Global Payments returned HTTP {}: {}", response.getStatusCode(), errorBody);
                    throw new GlobalPayException(response.getStatusCode().value(), errorBody);
                })
                .toEntity(AccessTokenResponse.class);
    }

    public String generateNonce() {
        return NONCE_FORMATTER.format(clock.instant());
    }

    public static String calculateSecret(String nonce, String appKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            String raw = nonce + appKey;
            byte[] hashBytes = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm not available", e);
        }
    }
}
