package com.zero.account.service;

import com.zero.account.exception.AccountException;
import com.zero.account.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class LockServiceTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RLock rLock;

    @InjectMocks
    private LockService lockService;

    @DisplayName("getLock 성공")
    @Test
    void successGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString())).willReturn(rLock);

        given(rLock.tryLock(anyLong(),anyLong(),any())).willReturn(true);
        //when
        assertDoesNotThrow(() -> lockService.lock("123456789"));
        //then

    }

    @DisplayName("getLock 실패")
    @Test
    void failGetLock() throws InterruptedException {
        //given
        given(redissonClient.getLock(anyString())).willReturn(rLock);
        given(rLock.tryLock(anyLong(),anyLong(),any())).willReturn(false);
        //when
        AccountException accountException = assertThrows(
                AccountException.class,
                () -> lockService.lock("123456789")
        );

        //then
        assertEquals(ErrorCode.ACCOUNT_TRANSACTION_LOCK, accountException.getErrorCode());
    }
}