package com.example.sholi.service.impl;

import com.example.sholi.service.UrlServiceRedis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UrlServiceRedisImpl implements UrlServiceRedis {

    private static final long TIMEOUT = 42;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public String findByUrl(String url) {
        return redisTemplate.opsForValue().get(url);
    }
    @Override
    public void saveUrl(String key, String value) {
        redisTemplate.opsForValue().set(key, value, TIMEOUT, TimeUnit.MINUTES);
    }

}
