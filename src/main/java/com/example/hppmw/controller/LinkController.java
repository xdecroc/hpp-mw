package com.example.hppmw.controller;

import com.example.hppmw.model.AccessTokenResponse;
import com.example.hppmw.model.LinkRequest;
import com.example.hppmw.model.LinkResponse;
import com.example.hppmw.service.GlobalPayAuthService;
import com.example.hppmw.service.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LinkController {

    private final LinkService linkService;
    private final GlobalPayAuthService globalPayAuthService;

    public LinkController(LinkService linkService, GlobalPayAuthService globalPayAuthService) {
        this.linkService = linkService;
        this.globalPayAuthService = globalPayAuthService;
    }

    /**
     * POST /link
     *
     * Accepts a Bearer token via the Authorization header and a {@link LinkRequest} body.
     * Forwards the request to the Global Payments hosted-payment-page link creation API
     * and returns the resulting {@link LinkResponse}.
     *
     * @param token       the raw token value (without the "Bearer " prefix)
     * @param linkRequest the link creation payload
     * @return the link details returned by Global Payments
     */
    @PostMapping("/link")
    public ResponseEntity<LinkResponse> createLink(
            @RequestHeader("Authorization") String token,
            @RequestBody LinkRequest linkRequest) {

        // Strip "Bearer " prefix if already present so the service always has the raw token
        String rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return linkService.createLink(rawToken, linkRequest);
    }

    @PostMapping("/hpplink")
    public ResponseEntity<LinkResponse> createLinkCombo(
            @RequestBody LinkRequest linkRequest) {

        String rawToken;

        ResponseEntity<AccessTokenResponse> accessEntity = globalPayAuthService.requestAccessToken();
        if(accessEntity.getStatusCode().is2xxSuccessful()){
            String token = accessEntity.getBody().token();
            // Strip "Bearer " prefix if already present so the service always has the raw token
            rawToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            return linkService.createLink(rawToken, linkRequest);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }
}
