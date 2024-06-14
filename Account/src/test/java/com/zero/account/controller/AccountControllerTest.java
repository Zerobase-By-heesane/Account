package com.zero.account.controller;

import com.zero.account.domain.Account;
import com.zero.account.domain.AccountStatus;
import com.zero.account.repository.AccountRepository;
import com.zero.account.service.AccountService;
import com.zero.account.service.RedisTestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("계좌 조회 성공")
    @Test
    void successGetAccount() throws Exception {
        //given
        given(accountRepository.findById(anyLong()))
                .willReturn(Optional.of(Account.builder()
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountNumber("65789").build()));

        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        //when
        Account account = accountService.getAccount(4555L);

        //then
        verify(accountRepository,times(1)).findById(captor.capture());
        verify(accountRepository,times(0)).save(any());
        assertEquals(4555L, captor.getValue());
        assertNotEquals(45515L, captor.getValue());
        assertEquals("65789", account.getAccountNumber());
        assertEquals(AccountStatus.IN_USE, account.getAccountStatus());
    }


}