package com.example.hppmw.controller;

import com.example.hppmw.model.LinkRequest;
import com.example.hppmw.model.LinkResponse;
import com.example.hppmw.service.LinkService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LinkController.class)
class LinkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LinkService linkService;

    private static final LinkResponse MOCK_RESPONSE = new LinkResponse(
            "LNK_JGWfx9L9Oc3RS2N5WobbocjMDuSqvm",
            "transaction_processing",
            "https://apis.sandbox.globalpay.com/ucp/hpp/redirect/42060877-8bf9-4ae3-a3ae-99a6da10cf41",
            "ACTIVE",
            "HOSTED_PAYMENT_PAGE",
            "SINGLE",
            "1",
            "Bill Payment",
            "February and March Invoice",
            "2024-06-11T14:09:23.577Z",
            new LinkResponse.Order("1000", "EUR", "order-645",
                    new LinkResponse.TransactionConfiguration("GB", "CNP")),
            new LinkResponse.Action("ACT_JGWfx9L9Oc3RS2N5WobbocjMDuSqvm", "LINK_CREATE",
                    "2024-06-10T14:09:23.577Z", "SUCCESS", "appId1", "demo_app")
    );

    private LinkRequest buildSampleRequest() {
        return new LinkRequest(
                "transaction_processing",
                "HOSTED_PAYMENT_PAGE",
                "Bill Payment",
                "February and March Invoice",
                "82c2dbd5-549d-4967-aeea-51b3f320046a",
                new LinkRequest.Payer("James Mason", "en", "jamesmason@example.com",
                        new LinkRequest.MobilePhone("44", "1801555888"),
                        new LinkRequest.BillingAddress("Flat 456", "House 456", "Unit 4",
                                "Halifax", "44", "GB")),
                new LinkRequest.Order("1000", "EUR", "order-645",
                        new LinkRequest.TransactionConfiguration("CNP", "GB", "AUTO", List.of("CARD")),
                        new LinkRequest.PaymentMethodConfiguration(
                                new LinkRequest.Authentication("CHALLENGE_PREFERRED"))),
                new LinkRequest.Notifications(
                        "https://www.example.com/return_url",
                        "https://www.example.com/status_url")
        );
    }

    @Test
    void testCreateLink_withBearerPrefixInHeader() throws Exception {
        when(linkService.createLink(eq("bBj5Bv4RoOz4xTJ7V5XtjLBHLN1h"), any(LinkRequest.class)))
                .thenReturn(ResponseEntity.ok(MOCK_RESPONSE));

        mockMvc.perform(post("/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer bBj5Bv4RoOz4xTJ7V5XtjLBHLN1h")
                        .content(objectMapper.writeValueAsString(buildSampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LNK_JGWfx9L9Oc3RS2N5WobbocjMDuSqvm"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.type").value("HOSTED_PAYMENT_PAGE"))
                .andExpect(jsonPath("$.order.currency").value("EUR"))
                .andExpect(jsonPath("$.action.result_code").value("SUCCESS"));
    }

    @Test
    void testCreateLink_withRawTokenInHeader() throws Exception {
        when(linkService.createLink(eq("rawtoken456"), any(LinkRequest.class)))
                .thenReturn(ResponseEntity.ok(MOCK_RESPONSE));

        mockMvc.perform(post("/link")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "rawtoken456")
                        .content(objectMapper.writeValueAsString(buildSampleRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("LNK_JGWfx9L9Oc3RS2N5WobbocjMDuSqvm"));
    }
}
