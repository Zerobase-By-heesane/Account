package com.zero.account.service;

import com.zero.account.exception.AccountException;
import com.zero.account.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class LockService {

    private final RedissonClient redissonClient;

    public void lock(String accountNumber){
        RLock lock = redissonClient.getLock(getLockKey(accountNumber));
        log.debug("Trying lock for AccountNumber : {}", getLockKey(accountNumber));

        try{
            boolean isLock = lock.tryLock(1,15, TimeUnit.SECONDS);
            if(!isLock){
                log.error("-------get Lock failed-------");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK, "Account is locked");
            }
        } catch (AccountException e) {
            throw e;
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    public void unlock(String accountNumber){
        log.debug("Unlock for accountNumber : {}",accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
