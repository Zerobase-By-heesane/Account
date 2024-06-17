package com.zero.account.dto;

import com.zero.account.domain.Account;
import lombok.*;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AccountInfo {
    private String accountNumber;
    private Long balance;
}
