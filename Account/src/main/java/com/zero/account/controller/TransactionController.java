package com.zero.account.controller;

import com.zero.account.dto.CreatedAccount;
import com.zero.account.dto.TransactionDTO;
import com.zero.account.dto.UseBalance;
import com.zero.account.exception.AccountException;
import com.zero.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * TransactionController
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래확인
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    public UseBalance.Response transactionUse(
            @RequestBody @Valid UseBalance.Request createdAccount
    ){
        try{
            return UseBalance.Response.toResponse(transactionService.saveTransaction(
                    createdAccount.getUserId(),
                    createdAccount.getAccountNumber(),
                    createdAccount.getAmount()
            ));
        } catch (AccountException ex){
            log.error("transactionUse error", ex);

            transactionService.saveFailedUseTransaction(
                    createdAccount.getAccountNumber(),
                    createdAccount.getAmount()
            );

            throw ex;
        }
    }
}
