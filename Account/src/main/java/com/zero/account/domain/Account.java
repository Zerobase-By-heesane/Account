package com.zero.account.domain;

import com.zero.account.exception.AccountException;
import com.zero.account.type.AccountStatus;
import com.zero.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;
    private Long balance;

    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

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
