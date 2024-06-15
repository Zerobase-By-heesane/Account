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


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    private final AccountUserRepository accountUserRepository;
    /**
     *
     * @param userId
     * 사용자 조회
     * @param initialBalance
     *
     * 계좌번호 생성
     * 계좌 저장 및 반환
     */
    @Transactional
    public AccountDTO createAccount(Long userId, Long initialBalance) {

        AccountUser accountUser = accountUserRepository.findById(userId).orElseThrow(
                () -> new AccountException(ErrorCode.USER_NOT_FOUND)
        );

        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc()
                .map(account -> Integer.valueOf(account.getAccountNumber()) + 1+"")
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

    @Transactional
    public Account getAccount(Long id){
        if(id<0){
            throw new RuntimeException("minus id");
        }
        return accountRepository.findById(id).get();
    }
}
