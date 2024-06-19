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
    AMOUNT_EXCEED_BALANCE("출금액이 잔액을 초과합니다."),
    TRANSACTION_NOT_FOUND("거래 내역이 존재하지 않습니다."),
    TRANSACTION_ACCOUNT_UNMATCHED("해당 거래내역의 계좌와 일치하지 않습니다."),
    TRANSACTION_ALREADY_CANCELED("이미 취소된 거래입니다."),
    TRANSACTION_ALREADY_USE("이미 사용된 거래입니다."),
    TRANSACTION_ALREADY_FAILED("이미 실패한 거래입니다."),
    TRANSACTION_ALREADY_CANCELED_OR_FAILED("이미 취소 또는 실패한 거래입니다."),
    CANCEL_MUST_FULLY("전체 취소만 가능합니다."),
    TOO_OLD_ORDER_TO_CANCEL("1년지 지난 거래는 취소할 수 없습니다."),
    INVALID_REQUEST("잘못된 요청입니다."), INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."), ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용 중입니다.");

    private final String description;
}
