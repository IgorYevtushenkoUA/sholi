package com.example.sholi.service;

public interface UrlServiceRedis {

    String findByUrl(String originalUrl);

    void saveUrl(String originalUrl, String shortenedKey);

}
