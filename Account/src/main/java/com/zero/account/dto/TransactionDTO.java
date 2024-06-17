package com.zero.account.dto;

import com.zero.account.domain.Transaction;
import com.zero.account.type.TransactionResultType;
import com.zero.account.type.TransactionType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    private String accountNumber;
    private TransactionType transactionType;

    private TransactionResultType transactionResultType;

    private Long amount;

    private Long balanceSnapshot;

    private String transactionId;

    private LocalDateTime transactedAt;

    public static TransactionDTO toTransactionDTO(Transaction transaction){
        return TransactionDTO.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapshot(transaction.getBalanceSnapshot())
                .transactionId(transaction.getTransactionId())
                .transactedAt(transaction.getTransactedAt())
                .build();
    }
}
