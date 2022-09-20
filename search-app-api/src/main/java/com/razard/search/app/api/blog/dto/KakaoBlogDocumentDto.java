package com.razard.search.app.api.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoBlogDocumentDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("contents")
    private String contents;

    @JsonProperty("url")
    private String url;

    @JsonProperty("blogname")
    private String blogname;

    @JsonProperty("thumbnail")
    private String thumbnail;

    @JsonProperty("datetime")
    private String datetime;

    @Builder
    public KakaoBlogDocumentDto(String title, String contents, String url, String blogname, String thumbnail, String datetime) {
        this.title = title;
        this.contents = contents;
        this.url = url;
        this.blogname = blogname;
        this.thumbnail = thumbnail;
        this.datetime = datetime;
    }
}
