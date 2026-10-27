package com.example.hppmw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class GlobalPayAuthServiceTest {

    private static final String APP_ID = "MN500Q8lk5jp0rEmB5LGvTo27gdQgZln";
    private static final String APP_KEY = "QtOSeNmh3DY6AuMO";
    private static final String BASE_URL = "https://apis.globalpay.com";
    private static final String ACCESS_TOKEN_URL = "/ucp/accesstoken";
    private static final String ENDPOINT = BASE_URL + ACCESS_TOKEN_URL;
    private static final String VERSION = "2021-03-22";

    // SHA-512("2029-03-14T13:24:10.832Z" + "QtOSeNmh3DY6AuMO")
    private static final String EXPECTED_SECRET =
            "e5ace5c560156e14ecce3e0cac133cdad3cedd18bfda3130f78fa47d4a829357a0ae112a036cac6ebd2f51c9a6de8062da78bf5b0d083738ae09d58ca95a95e8";

    private MockRestServiceServer mockServer;
    private GlobalPayAuthService authService;

    @BeforeEach
    void setUp() {
        // Bind mock BEFORE calling builder.build() so the factory is captured
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient testRestClient = builder.build();

        Clock fixedClock = Clock.fixed(Instant.parse("2029-03-14T13:24:10.832Z"), ZoneOffset.UTC);

        authService = new GlobalPayAuthService(
                testRestClient, APP_ID, APP_KEY, BASE_URL, ACCESS_TOKEN_URL, VERSION, fixedClock);
    }

    @Test
    void testCalculateSecret() {
        String secret = GlobalPayAuthService.calculateSecret("2029-03-14T13:24:10.832Z", APP_KEY);
        assertThat(secret).isEqualTo(EXPECTED_SECRET);
    }

    @Test
    void testGenerateNonce() {
        assertThat(authService.generateNonce()).isEqualTo("2029-03-14T13:24:10.832Z");
    }

    @Test
    void testRequestAccessToken_Success() {
        String mockBody = """
                {
                  "token": "test_access_token_123",
                  "type": "Bearer",
                  "app_id": "MN500Q8lk5jp0rEmB5LGvTo27gdQgZln",
                  "app_name": "test_app",
                  "time_created": "2029-03-14T13:24:10.832Z",
                  "seconds_to_expire": 3600,
                  "email": "test@example.com"
                }
                """;

        mockServer.expect(requestTo(ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header("X-GP-Version", VERSION))
                .andExpect(jsonPath("$.app_id").value(APP_ID))
                .andExpect(jsonPath("$.nonce").value("2029-03-14T13:24:10.832Z"))
                .andExpect(jsonPath("$.secret").value(EXPECTED_SECRET))
                .andExpect(jsonPath("$.grant_type").value("client_credentials"))
                .andRespond(withSuccess(mockBody, MediaType.APPLICATION_JSON));

        ResponseEntity<?> response = authService.requestAccessToken();

        mockServer.verify();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void testRequestAccessToken_UpstreamError_ThrowsGlobalPayException() {
        String errorBody = """
                {"error_code":"ACTION_NOT_AUTHORIZED","detailed_error_code":"40004"}
                """;

        mockServer.expect(requestTo(ENDPOINT))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .body(errorBody)
                        .contentType(MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> authService.requestAccessToken())
                .isInstanceOf(GlobalPayException.class)
                .satisfies(ex -> {
                    GlobalPayException gpe = (GlobalPayException) ex;
                    assertThat(gpe.getStatusCode()).isEqualTo(403);
                    assertThat(gpe.getResponseBody()).contains("ACTION_NOT_AUTHORIZED");
                });

        mockServer.verify();
    }
}
