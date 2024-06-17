package com.zero.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_ACCOUNT_PER_USER_10("사용자당 계좌는 최대 10개까지 생성 가능합니다."),
    ACCOUNT_NOT_FOUND("계좌가 존재하지 않습니다."),
    USER__ACCOUNT_UNMATCHED("사용자와 계좌의 주인이 일치하지 않습니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 비활성 상태입니다."),
    BALANCE_NOT_EMPTY("계좌 잔액이 0이 아닙니다."),
    AMOUNT_EXCEED_BALANCE("출금액이 잔액을 초과합니다.");

    private final String description;
}
