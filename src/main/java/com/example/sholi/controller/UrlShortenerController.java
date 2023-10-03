package com.example.sholi.controller;

import com.example.sholi.service.UrlServiceMongo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequestMapping("/api/url")
@RestController
public class UrlShortenerController {

    private static final String URL_PATTERN = "^(https?://)(.*)$";
    Logger logger = LogManager.getLogger(UrlShortenerController.class);

    @Autowired
    UrlServiceMongo urlService;

    @GetMapping("/{shUrl}")
    public String getOriginUrl(@PathVariable String shUrl) {
        validateUrl(shUrl, true);
        return Optional.ofNullable(urlService.findByShortenedUrl(shUrl)).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Url by short link was not found"));
    }

    @PostMapping("/create")
    public String create(@RequestBody String url) {
        validateUrl(url, false);
        return urlService.saveUrl(url);
    }

    private void validateUrl(String url, boolean isShortenedUrl) {
        if (StringUtils.isEmpty(url)) {
            logger.error("Url is empty");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Url is empty");
        }

        Pattern pattern = Pattern.compile(URL_PATTERN);
        Matcher matcher = pattern.matcher(url);
        if (!isShortenedUrl && !matcher.matches()) {
            logger.error("Incorrect Url");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect Url");
        }
        logger.debug("Url is valid");
    }
}
