package com.example.hppmw.controller;

import com.example.hppmw.model.AccessTokenResponse;
import com.example.hppmw.service.GlobalPayAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccessTokenController {

    private final GlobalPayAuthService authService;

    public AccessTokenController(GlobalPayAuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/access-token")
    public ResponseEntity<AccessTokenResponse> getAccessToken() {
        return authService.requestAccessToken();
    }
}
