package com.example.hppmw.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LinkResponse(

    @JsonProperty("id")
    String id,

    @JsonProperty("account_name")
    String accountName,

    @JsonProperty("url")
    String url,

    @JsonProperty("status")
    String status,

    @JsonProperty("type")
    String type,

    @JsonProperty("usage_mode")
    String usageMode,

    @JsonProperty("usage_limit")
    String usageLimit,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description,

    @JsonProperty("expiration_date")
    String expirationDate,

    @JsonProperty("order")
    Order order,

    @JsonProperty("action")
    Action action
) {

    public record Order(

        @JsonProperty("amount")
        String amount,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("reference")
        String reference,

        @JsonProperty("transaction_configuration")
        TransactionConfiguration transactionConfiguration
    ) {}

    public record TransactionConfiguration(

        @JsonProperty("country")
        String country,

        @JsonProperty("channel")
        String channel
    ) {}

    public record Action(

        @JsonProperty("id")
        String id,

        @JsonProperty("type")
        String type,

        @JsonProperty("time_created")
        String timeCreated,

        @JsonProperty("result_code")
        String resultCode,

        @JsonProperty("app_id")
        String appId,

        @JsonProperty("app_name")
        String appName
    ) {}
}
