package com.example.hppmw.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record LinkRequest(

    @JsonProperty("account_name")
    String accountName,

    @JsonProperty("type")
    String type,

    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description,

    @JsonProperty("reference")
    String reference,

    @JsonProperty("payer")
    Payer payer,

    @JsonProperty("order")
    Order order,

    @JsonProperty("notifications")
    Notifications notifications
) {

    public record Payer(

        @JsonProperty("name")
        String name,

        @JsonProperty("language")
        String language,

        @JsonProperty("email")
        String email,

        @JsonProperty("mobile_phone")
        MobilePhone mobilePhone,

        @JsonProperty("billing_address")
        BillingAddress billingAddress
    ) {}

    public record MobilePhone(

        @JsonProperty("country_code")
        String countryCode,

        @JsonProperty("subscriber_number")
        String subscriberNumber
    ) {}

    public record BillingAddress(

        @JsonProperty("line_1")
        String line1,

        @JsonProperty("line_2")
        String line2,

        @JsonProperty("line_3")
        String line3,

        @JsonProperty("city")
        String city,

        @JsonProperty("postal_code")
        String postalCode,

        @JsonProperty("country")
        String country
    ) {}

    public record Order(

        @JsonProperty("amount")
        String amount,

        @JsonProperty("currency")
        String currency,

        @JsonProperty("reference")
        String reference,

        @JsonProperty("transaction_configuration")
        TransactionConfiguration transactionConfiguration,

        @JsonProperty("payment_method_configuration")
        PaymentMethodConfiguration paymentMethodConfiguration
    ) {}

    public record TransactionConfiguration(

        @JsonProperty("channel")
        String channel,

        @JsonProperty("country")
        String country,

        @JsonProperty("capture_mode")
        String captureMode,

        @JsonProperty("allowed_payment_methods")
        List<String> allowedPaymentMethods
    ) {}

    public record PaymentMethodConfiguration(

        @JsonProperty("authentication")
        Authentication authentication
    ) {}

    public record Authentication(

        @JsonProperty("preference")
        String preference
    ) {}

    public record Notifications(

        @JsonProperty("return_url")
        String returnUrl,

        @JsonProperty("status_url")
        String statusUrl
    ) {}
}
