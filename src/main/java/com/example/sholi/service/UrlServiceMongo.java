package com.example.sholi.service;

public interface UrlServiceMongo {

    String findByShortenedUrl(String key);
    String saveUrl(String url);

}
