package com.example.hppmw.service;

import com.example.hppmw.model.LinkRequest;
import com.example.hppmw.model.LinkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Service
public class LinkService {

    private static final Logger log = LoggerFactory.getLogger(LinkService.class);

    private final RestClient restClient;
    private final String endpointUrl;
    private final String gpVersion;

    public LinkService(
            RestClient restClient,
            @Value("${globalpay.base.url:https://apis.sandbox.globalpay.com}") String baseUrl,
            @Value("${globalpay.ucp.link.url:/ucp/links}") String linkUrl,
            @Value("${globalpay.ucp.version:2021-03-22}") String gpVersion) {
        this.restClient = restClient;
        this.endpointUrl = baseUrl + linkUrl;
        this.gpVersion = gpVersion;
    }

    public ResponseEntity<LinkResponse> createLink(String token, LinkRequest linkRequest) {
        log.info("Creating payment link, type={} name={}", linkRequest.type(), linkRequest.name());

        return restClient.post()
                .uri(endpointUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("x-gp-version", gpVersion)
                .header("Connection", "keep-alive")
                .header("Authorization", "Bearer " + token)
                .body(linkRequest)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), (request, response) -> {
                    String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
                    log.warn("Global Payments links API returned HTTP {}: {}", response.getStatusCode(), errorBody);
                    throw new GlobalPayException(response.getStatusCode().value(), errorBody);
                })
                .toEntity(LinkResponse.class);
    }
}
