package com.zero.account.controller;

import com.zero.account.aop.AccountLock;
import com.zero.account.dto.CancelBalance;
import com.zero.account.dto.QueryTransactionResponse;
import com.zero.account.dto.UseBalance;
import com.zero.account.exception.AccountException;
import com.zero.account.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    @AccountLock
    public UseBalance.Response useBalance(
            @RequestBody @Valid UseBalance.Request useBalanceRequest
    ) throws InterruptedException {
        try{
            Thread.sleep(5000L);
            return UseBalance.Response.toResponse(transactionService.useBalance(
                    useBalanceRequest.getUserId(),
                    useBalanceRequest.getAccountNumber(),
                    useBalanceRequest.getAmount()
            ));
        } catch (AccountException ex){
            log.error("transactionUse error", ex);

            transactionService.saveFailedUseTransaction(
                    useBalanceRequest.getAccountNumber(),
                    useBalanceRequest.getAmount()
            );

            throw ex;
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @RequestBody @Valid CancelBalance.Request useBalanceRequest
    ){
        try{
            return CancelBalance.toResponse(transactionService.cancelBalance(
                    useBalanceRequest.getTransactionId(),
                    useBalanceRequest.getAccountNumber(),
                    useBalanceRequest.getAmount()
            ));
        } catch (AccountException ex){
            log.error("transactionCancel error", ex);

            transactionService.saveFailedCancelTransaction(
                    useBalanceRequest.getAccountNumber(),
                    useBalanceRequest.getAmount()
            );

            throw ex;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse.Response getTransactionInfo(
            @PathVariable String transactionId
    ){
        transactionService.queryTransaction(transactionId);
        return QueryTransactionResponse.toResponse(
                transactionService.queryTransaction(transactionId)
        );
    }
}
