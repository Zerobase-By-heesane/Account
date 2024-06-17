package com.zero.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zero.account.dto.AccountDTO;
import com.zero.account.dto.CreatedAccount;
import com.zero.account.service.AccountService;
import com.zero.account.service.RedisTestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AccountController.class)
class AccountControllerTest {


    @MockBean
    private RedisTestService redisTestService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("")
    @Test
    void successCreateAccount() throws Exception {
        //given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDTO.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when
        //then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreatedAccount.Request(3333L, 1111L)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());

    }


//    @DisplayName("계좌 조회 성공")
//    @Test
//    void successGetAccount() throws Exception {
//        //given
//        given(accountRepository.findById(anyLong()))
//                .willReturn(Optional.of(Account.builder()
//                        .accountStatus(AccountStatus.UNREGISTERED)
//                        .accountNumber("65789").build()));
//
//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
//
//        //when
//        Account account = accountService.getAccount(4555L);
//
//        //then
//        verify(accountRepository,times(1)).findById(captor.capture());
//        verify(accountRepository,times(0)).save(any());
//        assertEquals(4555L, captor.getValue());
//        assertNotEquals(45515L, captor.getValue());
//        assertEquals("65789", account.getAccountNumber());
//        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
//    }


}