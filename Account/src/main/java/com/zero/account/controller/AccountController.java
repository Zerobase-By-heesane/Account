package com.zero.account.controller;

import com.zero.account.domain.Account;
import com.zero.account.dto.AccountDTO;
import com.zero.account.service.AccountService;
import com.zero.account.service.RedisTestService;
import com.zero.account.dto.CreatedAccount;
import com.zero.account.dto.CreatedAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor

public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @PostMapping("/account")
    public CreatedAccount.Response createAccount(
            @RequestBody @Valid CreatedAccount.Request createdAccount
    ){
        AccountDTO savedAccountDTO = accountService.createAccount(createdAccount.getUserId(), createdAccount.getInitialBalance());

        return CreatedAccount.Response.toResponse(savedAccountDTO);
    }

    @GetMapping("/get-lock")
    public String getLock(){
        return redisTestService.getLock();
    }


    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id){
        return accountService.getAccount(id);
    }
}
