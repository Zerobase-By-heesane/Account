package com.zero.account.controller;

import com.zero.account.domain.Account;
import com.zero.account.service.AccountService;
import com.zero.account.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor

public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @GetMapping("/get-lock")
    public String getLock(){
        return redisTestService.getLock();
    }

    @GetMapping("/create-account")
    public String createAccount(){
        accountService.createAccount();
        return "Account created";
    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){
        return accountService.getAccount(id);
    }
}
