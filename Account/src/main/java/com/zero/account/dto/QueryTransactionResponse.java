package com.zero.account.dto;

import com.zero.account.type.TransactionResultType;
import com.zero.account.type.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class QueryTransactionResponse {
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Response {

        private String accountNumber;
        private TransactionType transactionType;
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
                transactionDTO.getTransactionType(),
                transactionDTO.getTransactionResultType(),
                transactionDTO.getTransactionId(),
                transactionDTO.getAmount(),
                transactionDTO.getTransactedAt());
    }
}
