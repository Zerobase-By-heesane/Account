package com.zero.account.service;

import com.zero.account.aop.AccountLockIdInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

    private final LockService lockService;

    // aspectj 라이브러리 사용
    // 어떤 경우에 사용할 것인가?
    @Around("@annotation(com.zero.account.aop.AccountLock) && args(request)")
    public Object aroundMethod(
            ProceedingJoinPoint pjp,
            AccountLockIdInterface request
    )throws Throwable{
        // lock 시도
        lockService.lock(request.getAccountNumber());
        try{
            // before
            return pjp.proceed();// 실제동작
            // after
        } finally {
            // lock 해제
            lockService.unlock(request.getAccountNumber());
        }
    }

}
