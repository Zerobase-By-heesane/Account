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
import com.zero.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.zero.account.type.AccountStatus.IN_USE;
import static com.zero.account.type.TransactionResultType.S;
import static com.zero.account.type.TransactionType.CANCEL;
import static com.zero.account.type.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    public static final long CANCEL_AMOUNT = 1000L;


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
    void successUseBalance() {
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
                        .accountStatus(IN_USE)
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .balance(1000L)
                        .build()));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(Account.builder()
                                .accountNumber("1000000002")
                                .accountStatus(IN_USE)
                                .accountUser(accountUser)
                                .accountStatus(IN_USE)
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
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(transactionDTO.getAccountNumber(), "1000000002");
        assertEquals(captor.getValue().getAmount(), 900L);
        assertEquals(transactionDTO.getTransactionId(), "1234567890");
        assertEquals(captor.getValue().getBalanceSnapshot(), 100L);

    }

    @DisplayName("결제 실패 - 유저가 없음")
    @Test
    void useBalance_UserNotFound() {
        //given
        given(accountUserRepository.findById(1L))
                .willReturn(Optional.empty());
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 계좌가 없음")
    @Test
    void useBalance_AccountNotFound() {
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
    void useBalance_AccountUserUnmatched() {
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
                        .accountStatus(IN_USE)
                        .accountUser(AccountUser.builder()
                                .id(2L)
                                .name("test2")
                                .build())
                        .accountStatus(IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 실패 - 계좌가 사용중이 아님")
    @Test
    void useBalance_AccountNotInUse() {
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
    void useBalance_AmountExceedBalance() {
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
                        .accountStatus(IN_USE)
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 1001L));
    }

    @DisplayName("결제 실패 - 소유주와 사용자가 다름")
    @Test
    void useBalance_OwnerAndUserDifferent() {
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
                        .accountStatus(IN_USE)
                        .accountUser(AccountUser.builder()
                                .id(2L)
                                .name("test2")
                                .build())
                        .accountStatus(IN_USE)
                        .balance(1000L)
                        .build()));
        //when
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 취소 성공")
    @Test
    void successCancelBalance() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        Account account = Account.builder()
                .accountUser(accountUser)
                .accountNumber("1000000002")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapshot(9000L)
                .build();



        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .amount(CANCEL_AMOUNT)
                        .transactionId("transactionId")
                        .transactionType(CANCEL)
                        .transactionResultType(S)
                        .balanceSnapshot(10000L)
                        .build());


        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);


        //when
        TransactionDTO transactionDTO = transactionService.cancelBalance(
                "transactionId", "1000000002", CANCEL_AMOUNT);
        //then
        verify(transactionRepository, times(1)).save(captor.capture());
        assertEquals(captor.getValue().getAmount(), CANCEL_AMOUNT);
        assertEquals(captor.getValue().getBalanceSnapshot(), 10000L + CANCEL_AMOUNT);
        assertEquals(S, captor.getValue().getTransactionResultType());
        assertEquals(CANCEL, captor.getValue().getTransactionType());
        assertEquals(transactionDTO.getAccountNumber(), "1000000002");
        assertEquals(transactionDTO.getTransactionId(), "transactionId");

    }

    @DisplayName("결제 취소 실패 - 잔액 사용 취소 실패")
    @Test
    void cancelTransaction_AccountNotFound() {
        //given

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        assertThrows(AccountException.class, () -> transactionService.cancelBalance("transactionId", "1000000002", 1000L));
        //then
        assertThrows(AccountException.class, () -> transactionService.useBalance(1L, "1000000002", 900L));
    }

    @DisplayName("결제 취소 실패 - 거래 내역이 존재하지 않는 경우")
    @Test
    void cancelTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class, () -> transactionService.cancelBalance("transactionId", "1000000002", 1000L));
        //then
        assertEquals(accountException.getErrorCode(), ErrorCode.TRANSACTION_NOT_FOUND);
    }

    @DisplayName("거래와 계좌가 매칭실패")
    @Test
    void cancelTransaction_UnMatching() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        Account account = Account.builder()
                .id(1L)
                .accountUser(accountUser)
                .accountNumber("1000000002")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Account account2 = Account.builder()
                .id(2L)
                .accountUser(accountUser)
                .accountNumber("1000000003")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account2));

        //when
        AccountException accountException = assertThrows(
                AccountException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000003",
                        1000L));
        //then
        assertEquals(accountException.getErrorCode(), ErrorCode.TRANSACTION_ACCOUNT_UNMATCHED);
    }

    @DisplayName("거래 취소 실패 - 취소 금액이 다름")
    @Test
    void cancelTransaction_AmountNotMatched() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        Account account = Account.builder()
                .id(1L)
                .accountUser(accountUser)
                .accountNumber("1000000002")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now())
                .amount(1000L)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException accountException = assertThrows(
                AccountException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000002",
                        1001L));
        //then
        assertEquals(accountException.getErrorCode(), ErrorCode.CANCEL_MUST_FULLY);
    }

    @DisplayName("1년 전 거래는 취소 불가")
    @Test
    void cancelTransaction_TooOldTransaction() {
        //given
        AccountUser accountUser = AccountUser.builder()
                .id(1L)
                .name("test")
                .build();

        Account account = Account.builder()
                .id(1L)
                .accountUser(accountUser)
                .accountNumber("1000000002")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(USE)
                .transactionResultType(S)
                .transactionId("transactionId")
                .transactedAt(LocalDateTime.now().minusYears(1).minusDays(1))
                .amount(1000L)
                .balanceSnapshot(9000L)
                .build();

        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.of(transaction));

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account));

        //when
        AccountException accountException = assertThrows(
                AccountException.class,
                () -> transactionService.cancelBalance(
                        "transactionId",
                        "1000000002",
                        1000L));
        //then
        assertEquals(accountException.getErrorCode(), ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
    }

    @DisplayName("Query Transaction 성공")
    @Test
    void successQueryTransaction(){
        //given
        Account account = Account.builder()
                .accountNumber("1000000002")
                .accountStatus(IN_USE)
                .balance(10000L)
                .build();

        Transaction transaction = Transaction.builder()
                .transactionId("transactionId")
                .amount(1000L)
                .balanceSnapshot(9000L)
                .transactionType(USE)
                .transactionResultType(S)
                .transactedAt(LocalDateTime.now())
                .account(account)
                .build();

        given(transactionRepository.findByTransactionId("transactionId"))
                .willReturn(Optional.of(transaction));
        //when
        TransactionDTO transactionDTO = transactionService.queryTransaction("transactionId");
        //then
        assertEquals(transactionDTO.getTransactionId(), "transactionId");
        assertEquals(transactionDTO.getAmount(), 1000L);
        assertEquals(transactionDTO.getBalanceSnapshot(), 9000L);
        assertEquals(transactionDTO.getTransactionType(), USE);
        assertEquals(transactionDTO.getTransactionResultType(), S);
        assertEquals(transactionDTO.getAccountNumber(), "1000000002");

    }

    @DisplayName("원거래 없음 - 거래 조회 실패")
    @Test
    void queryTransaction_TransactionNotFound() {
        //given
        given(transactionRepository.findByTransactionId(anyString()))
                .willReturn(Optional.empty());

        //when
        AccountException accountException = assertThrows(AccountException.class, () -> transactionService.queryTransaction("transactionId"));
        //then
        assertEquals(accountException.getErrorCode(), ErrorCode.TRANSACTION_NOT_FOUND);
    }
}