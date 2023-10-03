package com.example.sholi.service.impl;

import com.example.sholi.entity.Url;
import com.example.sholi.repository.UrlRepository;
import com.example.sholi.service.UrlServiceMongo;
import com.example.sholi.service.UrlServiceRedis;
import com.google.common.hash.Hashing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class UrlServiceMongoImpl implements UrlServiceMongo {
    Logger logger = LogManager.getLogger(UrlServiceMongoImpl.class);

    @Autowired
    UrlRepository urlRepository;

    @Autowired
    UrlServiceRedis serviceRedis;

    @Override
    public String findByShortenedUrl(String shortenedUrl) {

        String redisUrl = serviceRedis.findByUrl(shortenedUrl);
        if (redisUrl != null) {
            logger.info(String.format("Url was found in Redis cache : %s", redisUrl));
            return redisUrl;
        }

        Url mongoUrl = urlRepository.findByShortenedUrl(shortenedUrl);
        if (mongoUrl != null) {
            saveToRedisCache(mongoUrl.getUrl(), shortenedUrl);
            return mongoUrl.getUrl();
        }
        return null;
    }

    @Override
    public String saveUrl(String url) {

        String redisUrl = serviceRedis.findByUrl(url);
        if (redisUrl != null) {
            logger.info(String.format("Url was found in Redis cache : %s", redisUrl));
            return redisUrl;
        }

        Url mongoUrl = urlRepository.findByUrl(url);

        if (mongoUrl == null) {
            logger.info(String.format("Url was found in MongoDB : %s", url));
            mongoUrl = urlRepository.save(Url.builder()
                    .url(url)
                    .shortenedUrl(calcShortenedUrlHash(url))
                    .build());
            logger.info(String.format("Url was saved in MongoDB : %s", url));
        }
        saveToRedisCache(url, mongoUrl.getShortenedUrl());
        return mongoUrl.getShortenedUrl();
    }

    private void saveToRedisCache(String url, String shortenedUrl) {
        logger.info(String.format("Url : %s and shortenedUrlHash : %s was saved in Redis cache", url, shortenedUrl));
        serviceRedis.saveUrl(url, shortenedUrl);
        serviceRedis.saveUrl(shortenedUrl, url);
    }

    private String calcShortenedUrlHash(String url) {
        return Hashing.murmur3_32().hashString(url, StandardCharsets.UTF_8).toString();
    }

}
