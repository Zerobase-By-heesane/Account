package com.zero.account.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisTestService {

    private final RedissonClient redissonClient;

    public String getLock(){
        RLock lock = redissonClient.getLock("sampleLock");


        try{
            boolean isLock = lock.tryLock(10,5, TimeUnit.SECONDS);
            if(!isLock){
                log.error("-------get Lock failed-------");
                return "get Lock failed";
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }

        return "get Lock success";
    }
}