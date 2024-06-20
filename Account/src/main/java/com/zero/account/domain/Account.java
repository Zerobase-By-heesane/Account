package com.zero.account.domain;

import com.zero.account.exception.AccountException;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.ErrorCode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;

    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    private Long balance;

    private LocalDateTime registeredAt;

    private LocalDateTime unRegisteredAt;

    public void unRegister() {
        this.accountStatus = AccountStatus.UNREGISTERED;
        this.unRegisteredAt = LocalDateTime.now();
    }

    public void useBalance(Long amount) {
        if(amount > this.balance) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        this.balance = this.balance - amount;
    }

    public void cancelUseBalance(Long amount) {
        if(amount < 0){
            throw new AccountException(ErrorCode.INVALID_REQUEST);
        }
        this.balance = this.balance + amount;
    }
}
