package com.zero.account.service;

import com.zero.account.domain.Account;
import com.zero.account.domain.AccountUser;
import com.zero.account.dto.AccountDTO;
import com.zero.account.exception.AccountException;
import com.zero.account.repository.AccountRepository;
import com.zero.account.repository.AccountUserRepository;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;

    @DisplayName("계좌 생성 성공")
    @Test
    void createAccountSuccess() {
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi").build();
        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));

        given(accountRepository.save(any(Account.class)))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000013").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);


        //when
        AccountDTO accountDTO = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDTO.getUserId());
        assertEquals("1000000013", captor.getValue().getAccountNumber());
    }


    @DisplayName("첫 계좌 생성")
    @Test
    void createFirstAccount() {
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();
        user.setId(15L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty());

        given(accountRepository.save(any(Account.class)))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);


        //when
        AccountDTO accountDTO = accountService.createAccount(1L, 1000L);

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDTO.getUserId());
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }

    @DisplayName("유저를 찾을 수 없습니다.")
    @Test
    void createAccount_UserNotFound() {
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L)
        );

        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @DisplayName("한 명의 유저당 가질수 있는 계좌는 최대 10개입니다.")
    @Test
    void createAccount_maxAccountIs10() {
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();

        user.setId(12L);

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.countByAccountUser(user))
                .willReturn(10);
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L)
        );
        //then

        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_10, accountException.getErrorCode());
    }

    @DisplayName("계좌 삭제 성공")
    @Test
    void deleteAccountSuccess(){
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();

        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000015")
                        .build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);


        //when
        AccountDTO accountDTO = accountService.deleteAccount(12L,"1000000015");

        //then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDTO.getUserId());
        assertEquals("1000000015", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());

    }

    @DisplayName("해당 유저 없음 - 계좌 해지 실패")
    @Test
    void deleteAccount_UserNotFound(){
        //given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1000000015")
        );
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }

    @DisplayName("해당 계좌 없음 - 계좌 해지 실패")
    @Test
    void deleteAccount_AccountNotFound(){
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(12L, "1000000015")
        );
        //then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, accountException.getErrorCode());
    }

    @DisplayName("해당 계좌 없음 - 계좌 소유주 다름")
    @Test
    void deleteAccount_UserUnMatch(){
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();
        user.setId(15L);
        AccountUser user2 = AccountUser.builder()
                .name("crong")
                .build();
        user.setId(16L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user2)
                        .balance(0L)
                        .accountNumber("1000000015")
                        .build()));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(15L, "1000000015")
        );
        //then
        assertEquals(ErrorCode.USER__ACCOUNT_UNMATCHED, accountException.getErrorCode());
    }

    @DisplayName("해재하려는 계좌는 잔액이 없어야한다.")
    @Test
    void deleteAccount_balanceIsNotEmpty(){
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();

        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(
                        Account.builder()
                                .accountUser(user)
                                .balance(1000L)
                                .accountNumber("1000000015")
                                .build()));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(15L, "1000000015")
        );
        //then
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, accountException.getErrorCode());
    }

    @DisplayName("해당 계좌가 이미 해지되었습니다.")
    @Test
    void deleteAccount_AlreadyUnRegistered(){
        //given
        AccountUser user = AccountUser.builder()
                .name("pobi")
                .build();
        user.setId(15L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(
                        Account.builder()
                                .accountUser(user)
                                .balance(0L)
                                .accountNumber("1000000015")
                                .accountStatus(AccountStatus.UNREGISTERED)
                                .build()));
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(15L, "1000000015")
        );
        //then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, accountException.getErrorCode());
    }

    @DisplayName("User ID로 계좌 조회 성공")
    @Test
    void successGetAccountByUserId(){
        //given

        AccountUser accountUser = AccountUser.builder()
                .name("test")
                .build();

        accountUser.setId(1L);
        List<Account> accountDTOs = List.of(
                Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("1234567890")
                        .balance(1000L)
                        .build(),
                Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("1111111111")
                        .balance(2000L)
                        .build(),
                Account.builder()
                        .accountUser(accountUser)
                        .accountNumber("2222222222")
                        .balance(3000L)
                        .build()
        );
        given(accountUserRepository.findById(anyLong())).willReturn(Optional.of(accountUser));
        given(accountRepository.findAllByAccountUser(any())).willReturn(accountDTOs);

        //when
        List<AccountDTO> accountDTOList = accountService.getAccountByUserId(1L);

        //then
        assertEquals(3, accountDTOList.size());
        assertEquals("1234567890", accountDTOList.get(0).getAccountNumber());
        assertEquals(1000L, accountDTOList.get(0).getBalance());
        assertEquals("1111111111", accountDTOList.get(1).getAccountNumber());
        assertEquals(2000L, accountDTOList.get(1).getBalance());
        assertEquals("2222222222", accountDTOList.get(2).getAccountNumber());
        assertEquals(3000L, accountDTOList.get(2).getBalance());
    }

    @DisplayName("AccountInfo 조회 실패")
    @Test
    void failToGetAccountInfo(){
        //given
        given(accountUserRepository.findById(anyLong())).willReturn(Optional.empty());
        //when
        AccountException accountException = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(15L, "1000000015"));
        //then
        assertEquals(ErrorCode.USER_NOT_FOUND, accountException.getErrorCode());
    }
}

