package com.example.sholi.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "Urls")
public class Url {

    @Id
    private String id;

    private String url;
    private String shortenedUrl;

}


