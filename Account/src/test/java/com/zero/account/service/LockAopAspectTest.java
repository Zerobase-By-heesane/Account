package com.zero.account.service;

import com.zero.account.dto.UseBalance;
import com.zero.account.exception.AccountException;
import com.zero.account.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

    @Mock
    private LockService lockService;

    @Mock
    private ProceedingJoinPoint pjp;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @DisplayName("lockAndUnlock")
    @Test
    void lockAndUnlock() throws Throwable{
        //given
        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L, "1234567890", 10000L);
        //when
        lockAopAspect.aroundMethod(pjp, request);
        //then
        verify(lockService,times(1)).lock(lockCaptor.capture());
        verify(lockService,times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234567890", lockCaptor.getValue());
        assertEquals("1234567890", unlockCaptor.getValue());
    }

    @DisplayName("lockAndUnlock_eventIfThrow")
    @Test
    void lockAndUnlock_eventIfThrow() throws Throwable{
        //given
        ArgumentCaptor<String> lockCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> unlockCaptor = ArgumentCaptor.forClass(String.class);

        UseBalance.Request request = new UseBalance.Request(123L, "1234567890", 10000L);

        given(pjp.proceed()).willThrow(new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));
        //when
        assertThrows(AccountException.class, () -> lockAopAspect.aroundMethod(pjp, request));

//        lockAopAspect.aroundMethod(pjp, request);
        //then
        verify(lockService,times(1)).lock(lockCaptor.capture());
        verify(lockService,times(1)).unlock(unlockCaptor.capture());

        assertEquals("1234567890", lockCaptor.getValue());
        assertEquals("1234567890", unlockCaptor.getValue());
    }

}