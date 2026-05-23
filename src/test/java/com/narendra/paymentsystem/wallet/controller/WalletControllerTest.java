package com.narendra.paymentsystem.wallet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.narendra.paymentsystem.wallet.dto.CreateWalletRequest;
import com.narendra.paymentsystem.wallet.entity.Wallet;
import com.narendra.paymentsystem.wallet.service.WalletService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(WalletController.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WalletService walletService;

    @Test
    void shouldCreateWalletSuccessfully()
            throws Exception {

        CreateWalletRequest request =
                new CreateWalletRequest(
                        "user1",
                        BigDecimal.valueOf(1000)
                );

        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user1")
                .balance(BigDecimal.valueOf(1000))
                .build();

        when(walletService.createWallet(any()))
                .thenReturn(wallet);

        mockMvc.perform(
                        post("/wallet/create")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                request
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId")
                        .value("user1"));
    }

    @Test
    void shouldGetWalletBalanceSuccessfully()
            throws Exception {

        Wallet wallet = Wallet.builder()
                .id(1L)
                .userId("user1")
                .balance(BigDecimal.valueOf(900))
                .build();

        when(walletService.getWallet("user1"))
                .thenReturn(wallet);

        mockMvc.perform(
                        get("/wallet/user1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance")
                        .value(900));
    }
}