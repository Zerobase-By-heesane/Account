package com.zero.account.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class CreatedAccount {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {

        @NotNull
        @Min(1)
        private Long userId;

        @NotNull
        @Min(100)
        private Long initialBalance;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        public static Response toResponse(AccountDTO accountDTO) {
            return Response.builder()
                    .userId(accountDTO.getUserId())
                    .accountNumber(accountDTO.getAccountNumber())
                    .registeredAt(accountDTO.getRegisteredAt())
                    .build();
        }
    }
}
