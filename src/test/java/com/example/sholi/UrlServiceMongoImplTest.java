package com.example.sholi;

import com.example.sholi.entity.Url;
import com.example.sholi.repository.UrlRepository;
import com.example.sholi.service.UrlServiceRedis;
import com.example.sholi.service.impl.UrlServiceMongoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UrlServiceMongoImplTest {

    private static final String URL = "https://google.com/";
    private static final String SHORTENED_URL = "23abcd1";

    @InjectMocks
    private UrlServiceMongoImpl urlService;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private UrlServiceRedis serviceRedis;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldNotFindUrlInRedisCacheAndInMongoDB() {
        when(serviceRedis.findByUrl(SHORTENED_URL)).thenReturn(null);
        when(urlRepository.findByShortenedUrl(SHORTENED_URL)).thenReturn(null);

        String result = urlService.findByShortenedUrl(SHORTENED_URL);

        assertEquals(result, null);
        verify(urlRepository).findByShortenedUrl(SHORTENED_URL);
    }

    @Test
    void shouldFindUrlInRedisCache() {
        when(serviceRedis.findByUrl(SHORTENED_URL)).thenReturn(URL);

        String result = urlService.findByShortenedUrl(SHORTENED_URL);

        assertEquals(URL, result);
        verify(serviceRedis).findByUrl(SHORTENED_URL);
    }

    @Test
    void shouldFindUrlInMongoDB() {
        Url url = buildUrl();

        when(serviceRedis.findByUrl(SHORTENED_URL)).thenReturn(null);
        when(urlRepository.findByShortenedUrl(SHORTENED_URL)).thenReturn(url);

        String result = urlService.findByShortenedUrl(SHORTENED_URL);

        assertEquals(URL, result);
        verify(serviceRedis).findByUrl(SHORTENED_URL);
        verify(urlRepository).findByShortenedUrl(SHORTENED_URL);
    }

    @Test
    void shouldSaveUrlToMongoDB() {
        Url savedUrl = buildUrl();
        when(serviceRedis.findByUrl(URL)).thenReturn(null);
        when(urlRepository.findByUrl(URL)).thenReturn(null);
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);

        String result = urlService.saveUrl(URL);

        assertEquals(savedUrl.getShortenedUrl(), result);
        verify(serviceRedis).findByUrl(URL);
        verify(urlRepository).findByUrl(URL);
        verify(urlRepository).save(argThat(url -> url.getUrl().equals(URL)));
    }

    @Test
    void shouldSaveUrlToRedisCached() {
        Url savedUrl = buildUrl();
        when(serviceRedis.findByUrl(URL)).thenReturn(null);
        when(urlRepository.findByUrl(URL)).thenReturn(savedUrl);

        String result = urlService.saveUrl(URL);

        assertEquals(savedUrl.getShortenedUrl(), result);
        verify(serviceRedis).findByUrl(URL);
        verify(urlRepository).findByUrl(URL);
    }

    @Test
    void shouldNotSaveUrlInRedisCacheOrMongoDB() {
        Url savedUrl = buildUrl();
        when(serviceRedis.findByUrl(URL)).thenReturn(savedUrl.getShortenedUrl());

        String result = urlService.saveUrl(URL);

        assertEquals(savedUrl.getShortenedUrl(), result);
        verify(serviceRedis).findByUrl(URL);
        verify(urlRepository, never()).findByUrl(anyString());
        verify(urlRepository, never()).save(savedUrl);
    }

    private Url buildUrl(){
        return Url.builder()
                .shortenedUrl(SHORTENED_URL)
                .url(URL)
                .build();
    }
}
