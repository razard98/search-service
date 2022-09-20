package com.razard.search.app.api.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NaverBlogDto {

    @JsonProperty("title")
    private String title;

    @JsonProperty("link")
    private String link;

    @JsonProperty("description")
    private String description;

    @JsonProperty("bloggername")
    private String bloggername;

    @JsonProperty("bloggerlink")
    private String bloggerlink;

    @JsonProperty("postdate")
    private String postdate;

    @Builder
    public NaverBlogDto(String title, String link, String description, String bloggername, String bloggerlink, String postdate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.bloggername = bloggername;
        this.bloggerlink = bloggerlink;
        this.postdate = postdate;
    }
}
