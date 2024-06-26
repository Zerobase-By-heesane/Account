package com.zero.account.dto;

import com.zero.account.aop.AccountLockIdInterface;
import com.zero.account.type.TransactionResultType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class UseBalance {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockIdInterface {
        @NotNull
        @Min(1)
        private Long userId;

        @NotBlank
        private String AccountNumber;

        @NotNull
        @Min(1)
        @Max(1_000_000_000L)
        private Long amount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response implements AccountLockIdInterface {


        private String accountNumber;
        private TransactionResultType transactionResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;


        public static Response toResponse(TransactionDTO transactionDTO) {
            return Response.builder()
                    .accountNumber(transactionDTO.getAccountNumber())
                    .transactionResultType(transactionDTO.getTransactionResultType())
                    .transactionId(transactionDTO.getTransactionId())
                    .amount(transactionDTO.getAmount())
                    .transactedAt(transactionDTO.getTransactedAt())
                    .build();
        }
    }
}
