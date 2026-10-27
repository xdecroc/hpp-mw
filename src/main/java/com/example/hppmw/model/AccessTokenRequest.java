package com.example.hppmw.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccessTokenRequest(
    @JsonProperty("app_id")
    String appId,

    @JsonProperty("nonce")
    String nonce,

    @JsonProperty("secret")
    String secret,

    @JsonProperty("grant_type")
    String grantType
) {
    public AccessTokenRequest(String appId, String nonce, String secret) {
        this(appId, nonce, secret, "client_credentials");
    }
}
