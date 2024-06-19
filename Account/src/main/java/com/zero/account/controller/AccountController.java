package com.zero.account.controller;

import com.zero.account.domain.Account;
import com.zero.account.dto.AccountDTO;
import com.zero.account.dto.AccountInfo;
import com.zero.account.dto.CreatedAccount;
import com.zero.account.dto.DeleteAccount;
import com.zero.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/account")
    public CreatedAccount.Response createAccount(
            @RequestBody @Valid CreatedAccount.Request createdAccount
    ) {
        AccountDTO savedAccountDTO = accountService.createAccount(createdAccount.getUserId(), createdAccount.getInitialBalance());

        return CreatedAccount.Response.toResponse(savedAccountDTO);
    }

    @DeleteMapping("/account")
    public DeleteAccount.Response createAccount(
            @RequestBody @Valid DeleteAccount.Request deleteAccount
    ){
        return DeleteAccount.Response.toResponse(
                accountService.deleteAccount(deleteAccount.getUserId(), deleteAccount.getAccountNumber())
        );
    }

    @GetMapping("/account")
    public List<AccountInfo> getAccountList(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountByUserId(userId)
                .stream()
                .map(accountDTO -> AccountInfo.builder()
                        .accountNumber(accountDTO.getAccountNumber())
                        .balance(accountDTO.getBalance())
                        .build())
                .collect(Collectors.toList());

    }

    @GetMapping("/account/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }
}
