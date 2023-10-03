package com.example.sholi.repository;

import com.example.sholi.entity.Url;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UrlRepository extends MongoRepository<Url, Integer> {
    Url findByShortenedUrl(String key);

    Url findByUrl(String url);

}
