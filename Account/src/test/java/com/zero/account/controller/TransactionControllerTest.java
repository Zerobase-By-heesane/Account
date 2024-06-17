package com.zero.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero.account.dto.TransactionDTO;
import com.zero.account.dto.UseBalance;
import com.zero.account.service.TransactionService;
import com.zero.account.type.TransactionResultType;
import com.zero.account.type.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void successUseBalance() throws Exception {
        //given
        given(transactionService.useBalance(anyLong(), anyString(), anyLong()))
                .willReturn(TransactionDTO.builder()
                        .accountNumber("1000000002")
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .amount(1000L)
                        .balanceSnapshot(1000L)
                        .transactionId("1234567890")
                        .transactedAt(LocalDateTime.now())
                        .build());

        //when
        //then
        mockMvc.perform(post("/transaction/use")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UseBalance.Request(1L, "1000000002", 900L))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1000000002"))
                .andExpect(jsonPath("$.transactionResultType").value(TransactionResultType.S.name()))
                .andExpect(jsonPath("$.transactionId").value("1234567890"))
                .andExpect(jsonPath("$.amount").value(1000L))
                .andExpect(jsonPath("$.transactedAt").exists());

    }
}