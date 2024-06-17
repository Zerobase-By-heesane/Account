package com.zero.account.service;

import com.zero.account.domain.Account;
import com.zero.account.domain.AccountUser;
import com.zero.account.domain.Transaction;
import com.zero.account.dto.TransactionDTO;
import com.zero.account.exception.AccountException;
import com.zero.account.repository.AccountRepository;
import com.zero.account.repository.AccountUserRepository;
import com.zero.account.repository.TransactionRepository;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.TransactionResultType;
import com.zero.account.type.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @DisplayName("결제 성공")
    @Test
    void successUseBalance(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000002")
                        .accountStatus(AccountStatus.IN_USE)
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L)
                        .build()));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(Account.builder()
                                .accountNumber("1000000002")
                                .accountStatus(AccountStatus.IN_USE)
                                .accountUser(accountUser)
                                .accountStatus(AccountStatus.IN_USE)
                                .balance(10000000L)
                                .build())
                        .amount(900L)
                        .balanceSnapshot(100L)
                        .transactionId("1234567890")
                        .build());

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


        //when
        TransactionDTO transactionDTO = transactionService.useBalance(
                1L, "1000000002", 900L);
        //then
        verify(transactionRepository,times(1)).save(captor.capture());
        assertEquals(transactionDTO.getAccountNumber(), "1000000002");
        assertEquals(captor.getValue().getAmount(), 900L);
        assertEquals(transactionDTO.getTransactionId(), "1234567890");
        assertEquals(captor.getValue().getBalanceSnapshot(), 100L);

    }

    @DisplayName("결제 실패 - 유저가 없음")
    @Test
    void useBalance_UserNotFound(){
        //given
        given(accountUserRepository.findById(1L))
                .willReturn(Optional.empty());
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 계좌가 없음")
    @Test
    void useBalance_AccountNotFound(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.empty());
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 계좌와 유저 불일치")
    @Test
    void useBalance_AccountUserUnmatched(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000002")
                        .accountStatus(AccountStatus.IN_USE)
                        .accountUser(AccountUser.builder()
                                .id(2L)
                                .name("test2")
                                .build())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 계좌가 사용중이 아님")
    @Test
    void useBalance_AccountNotInUse(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000002")
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 잔액 부족")
    @Test
    void useBalance_AmountExceedBalance(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000002")
                        .accountStatus(AccountStatus.IN_USE)
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 1001L));
    }

    @DisplayName("결제 실패 - 소유주와 사용자가 다름")
    @Test
    void useBalance_OwnerAndUserDifferent(){
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        given(accountUserRepository.findById(1L))
                .willReturn(Optional.of(accountUser));

        given(accountRepository.findByAccountNumber("1000000002"))
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000002")
                        .accountStatus(AccountStatus.IN_USE)
                        .accountUser(AccountUser.builder()
                                .id(2L)
                                .name("test2")
                                .build())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }
}