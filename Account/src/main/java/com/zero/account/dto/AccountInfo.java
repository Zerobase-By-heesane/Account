package com.zero.account.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;
}
