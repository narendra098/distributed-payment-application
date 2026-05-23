package com.narendra.paymentsystem.integrationtests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.narendra.paymentsystem.wallet.dto.CreateWalletRequest;
import com.narendra.paymentsystem.orchestrator.dto.TransferRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransferIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldTransferMoneySuccessfully()
            throws Exception {

        // Create sender wallet
        CreateWalletRequest sender =
                new CreateWalletRequest(
                        "integration-user-1",
                        BigDecimal.valueOf(1000)
                );

        mockMvc.perform(
                post("/wallet/create")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
                                        sender
                                )
                        )
        ).andExpect(status().isOk());

        // Create receiver wallet
        CreateWalletRequest receiver =
                new CreateWalletRequest(
                        "integration-user-2",
                        BigDecimal.valueOf(500)
                );

        mockMvc.perform(
                post("/wallet/create")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
                                        receiver
                                )
                        )
        ).andExpect(status().isOk());

        // Transfer money
        TransferRequest transferRequest =
                new TransferRequest(
                        "integration-user-1",
                        "integration-user-2",
                        BigDecimal.valueOf(100)
                );

        mockMvc.perform(
                post("/transfer")
                        .header(
                                "Idempotency-Key",
                                "integration-test-1"
                        )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(
                                objectMapper.writeValueAsString(
                                        transferRequest
                                )
                        )
        ).andExpect(status().isOk());
    }
}