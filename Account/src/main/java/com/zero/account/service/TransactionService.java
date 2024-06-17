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
    public TransactionDTO saveTransaction(Long userId,
                                          String accountNumber,
                                          Long amount ) {
        AccountUser accountUser = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUesBalance(account, accountUser, amount);

        account.useBalance(amount);

        return TransactionDTO.toTransactionDTO(saveAndGetTransaction(TransactionResultType.S, account, amount));
    }

    private void validateUesBalance(Account account,
                                    AccountUser accountUser,
                                    Long amount) {
        if(!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER__ACCOUNT_UNMATCHED);
        }

        if(account.getAccountStatus() != AccountStatus.IN_USE){
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if(account.getBalance() < amount) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber,
                                         Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(TransactionResultType.F, account, amount);
    }

    private Transaction saveAndGetTransaction(TransactionResultType transactionResultType,
                                              Account account,
                                              Long amount) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionType(TransactionType.USE)
                        .transactionResultType(transactionResultType)
                        .account(account)
                        .amount(amount)
                        .balanceSnapshot(account.getBalance())
                        .transactionId(UUID.randomUUID().toString().replace("-", ""))
                        .transactedAt(LocalDateTime.now())
                        .build()
        );
    }
}
