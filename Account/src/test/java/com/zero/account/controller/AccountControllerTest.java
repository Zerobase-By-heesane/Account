package com.zero.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero.account.domain.Account;
import com.zero.account.dto.AccountDTO;
import com.zero.account.dto.CreatedAccount;
import com.zero.account.dto.DeleteAccount;
import com.zero.account.exception.AccountException;
import com.zero.account.service.AccountService;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("계좌 생성 성공")
    @Test
    void successCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDTO.builder()
                        .userId(3333L)
                        .accountNumber("1234567890")
                        .balance(10000L)
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatedAccount.Request(3333L, 10000L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(3333L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }

    @DisplayName("계좌 삭제 성공")
    @Test
    void successDeleteAccount() throws Exception {
        //given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDTO.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(3333L, "1234567890")))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }

    @DisplayName("계좌 조회 성공 - QueryParam")
    @Test
    void successGetAccount() throws Exception {
        //given
        List<AccountDTO> accountDTOs = List.of(
                AccountDTO.builder()
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build(),
                AccountDTO.builder()
                        .accountNumber("1111111111")
                        .balance(1000L)
                        .build(),
                AccountDTO.builder()
                        .accountNumber("2222222222")
                        .balance(1000L)
                        .build()
        );
        given(accountService.getAccountByUserId(anyLong()))
                .willReturn(accountDTOs);

        //when
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value(1000L))
                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[1].balance").value(1000L))
                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[2].balance").value(1000L));
    }

    @DisplayName("계좌 조회 성공 - PathVariable")
    @Test
    void GetAccountInfo() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willReturn(Account.builder()
                        .accountNumber("1234567890")
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L).build());
        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andExpect(jsonPath("$.accountStatus").value("IN_USE"));
    }

    @DisplayName("계좌 조회 실패 - 계좌 존재하지 않음")
    @Test
    void failGetAccount2() throws Exception {
        //given
        given(accountService.getAccount(anyLong()))
                .willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        //when
        //then
        mockMvc.perform(get("/account/876"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_NOT_FOUND"))
                .andExpect(jsonPath("$.errorMessage").value("계좌가 존재하지 않습니다."));
    }
}