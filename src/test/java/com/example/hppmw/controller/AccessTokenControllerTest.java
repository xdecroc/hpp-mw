package com.example.hppmw.controller;

import com.example.hppmw.model.AccessTokenResponse;
import com.example.hppmw.service.GlobalPayAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccessTokenController.class)
class AccessTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GlobalPayAuthService authService;

    @Test
    void testGetAccessTokenEndpoint() throws Exception {
        AccessTokenResponse mockResponse = new AccessTokenResponse(
                "test_token_abc",
                "Bearer",
                "MN500Q8lk5jp0rEmB5LGvTo27gdQgZln",
                "demo_app",
                "2029-03-14T13:24:10.832Z",
                3600L,
                "test@example.com"
        );

        when(authService.requestAccessToken())
                .thenReturn(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(mockResponse));

        mockMvc.perform(get("/access-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test_token_abc"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.app_name").value("demo_app"));
    }
}
