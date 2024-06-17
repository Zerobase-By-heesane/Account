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
import com.zero.account.type.TransactionResultType;
import com.zero.account.type.TransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public TransactionDTO useBalance(Long userId,
                                     String accountNumber,
                                     Long amount) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUesBalance(account, accountUser, amount);

        account.useBalance(amount);

        return TransactionDTO.toTransactionDTO(saveAndGetTransaction(TransactionResultType.S, account, amount,TransactionType.USE));
    }

    private void validateUesBalance(Account account,
                                    AccountUser accountUser,
                                    Long amount) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER__ACCOUNT_UNMATCHED);
        }

        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber,
                                         Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, account, amount,TransactionType.USE);
    }



    @Transactional
    public TransactionDTO cancelBalance(String transactionId,
                                        String accountNumber,
                                        Long amount) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateCancelBalance(account, transaction, amount);

        account.cancelUseBalance(amount);

        return TransactionDTO.toTransactionDTO(saveAndGetTransaction(TransactionResultType.S, account, amount,TransactionType.CANCEL));
    }

    @Transactional
    public void saveFailedCancelTransaction(String accountNumber,
                                           Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, account, amount,TransactionType.CANCEL);
    }

    private void validateCancelBalance(Account account, Transaction transaction, Long amount) {
        if (!Objects.equals(account.getId(), transaction.getAccount().getId())) {
            throw new AccountException(ErrorCode.TRANSACTION_ACCOUNT_UNMATCHED);
        }

        if (!Objects.equals(transaction.getAmount(), amount)) {
            throw new AccountException(ErrorCode.CANCEL_MUST_FULLY);
        }

        if (transaction.getTransactedAt().isBefore(LocalDateTime.now().minusYears(1))) {
            throw new AccountException(ErrorCode.TOO_OLD_ORDER_TO_CANCEL);
        }
    }

    private Transaction saveAndGetTransaction(TransactionResultType transactionResultType,
                                              Account account,
                                              Long amount,
                                              TransactionType transactionType) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(transactionType)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }

    public TransactionDTO queryTransaction(String transactionId) {
        return TransactionDTO.toTransactionDTO(
                transactionRepository.findByTransactionId(transactionId)
                        .orElseThrow(() -> new AccountException(ErrorCode.TRANSACTION_NOT_FOUND))
        );
    }
}
