package com.zero.account.service;

import com.zero.account.domain.Account;
import com.zero.account.domain.AccountStatus;
import com.zero.account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void createAccount() {
        Account account = Account.builder()
                .accountNumber("1234")
                .accountStatus(AccountStatus.IN_USE)
                .build();
        accountRepository.save(account);
    }

    @Transactional
    public Account getAccount(Long id){
        if(id<0){
            throw new RuntimeException("minus id");
        }
        return accountRepository.findById(id).get();
    }
}
