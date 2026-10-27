package com.example.hppmw.service;

/**
 * Wraps a non-2xx HTTP response from the Global Payments access-token endpoint,
 * preserving the upstream status code and raw error body.
 */
public class GlobalPayException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public GlobalPayException(int statusCode, String responseBody) {
        super("Global Payments error " + statusCode + ": " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
