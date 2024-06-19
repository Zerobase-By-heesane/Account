package com.zero.account.dto;

import com.zero.account.aop.AccountLockIdInterface;
import com.zero.account.type.TransactionResultType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


public class CancelBalance {

    /**
     * {
     * "transactionId": "dsfasdgasgdfgfdaasfd",
     * "accountNumber": "1234567890",
     * "amount": 10000
     * }
     */

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {

        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1_000_000_000L)
        private Long amount;
    }

    /**
     * {
     * "accountNumber": "1234567890",
     * "transactionResultType": "CANCEL",
     * "transactionId": "dsfasdgasgdfgfdaasfd",
     * "amount": 10000,
     * "transactedAt": "2021-08-01T00:00:00"
     * }
     */

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response implements AccountLockIdInterface{

        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;
//        private Long onlyForUse;
//        private Long onlyForCancel;
    }

    public static Response toResponse(TransactionDTO transactionDTO) {
        return new Response(
                transactionDTO.getAccountNumber(),
                transactionDTO.getTransactionResultType(),
                transactionDTO.getTransactionId(),
                transactionDTO.getAmount(),
                transactionDTO.getTransactedAt());
    }
}
