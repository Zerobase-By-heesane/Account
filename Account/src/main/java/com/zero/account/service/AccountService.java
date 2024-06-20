package com.zero.account.service;

import com.zero.account.domain.Account;
import com.zero.account.domain.AccountUser;
import com.zero.account.dto.AccountDTO;
import com.zero.account.exception.AccountException;
import com.zero.account.repository.AccountRepository;
import com.zero.account.repository.AccountUserRepository;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountUserRepository accountUserRepository;

    @Transactional
    public AccountDTO createAccount(Long userId, Long initialBalance) {

        AccountUser accountUser = getAccountUser(userId);

        validateCreateAccount(accountUser);

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> Integer.valueOf(account.getAccountNumber()) + 1 + "")
                .orElse("1000000000");

        return AccountDTO.toAccountDto(accountRepository.save(
                Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(AccountStatus.IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build()));
    }

    private void validateCreateAccount(AccountUser accountUser) {
        if (accountRepository.countByAccountUser(accountUser) == 10) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_10);
        }
    }

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("minus id");
        }
        return accountRepository.findById(id).get();
    }

    @Transactional
    public AccountDTO deleteAccount(Long userId, String accountNumber) {

        AccountUser accountUser = getAccountUser(userId);

        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow(
                () -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND)
        );

        validateDeleteAccount(accountUser, account);

        account.unRegister();

        accountRepository.save(account);
        return AccountDTO.toAccountDto(account);
    }

    private AccountUser getAccountUser(Long userId) {
        return accountUserRepository.findById(userId).orElseThrow(
                () -> new AccountException(ErrorCode.USER_NOT_FOUND)
        );
    }

    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (!Objects.equals(accountUser.getId(), account.getAccountUser().getId())) {
            throw new AccountException(ErrorCode.USER__ACCOUNT_UNMATCHED);
        }

        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() != 0) {
            throw new AccountException(ErrorCode.BALANCE_NOT_EMPTY);
        }
    }

    @Transactional
    public List<AccountDTO> getAccountByUserId(Long userId) {
        AccountUser accountUser = getAccountUser(userId);

        List<Account> accounts = accountRepository.findAllByAccountUser(accountUser);

        return accounts.stream()
                .map(AccountDTO::toAccountDto)
                .collect(Collectors.toList());
    }
}
