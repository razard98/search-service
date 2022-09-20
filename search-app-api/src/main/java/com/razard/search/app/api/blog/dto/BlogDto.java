package com.razard.search.app.api.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * Blog DTO
 */
public class BlogDto {

    @Data
    @Builder
    public static class SearchRequest {

        private String query;

        private String sort;        /* accuracy : 정확도, recency: 최신순 */

        private Integer page;

        private Integer size;

    }

    @Getter
    public static class SearchResponse {

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
        public SearchResponse(String title, String contents, String url, String blogname, String thumbnail, String datetime) {
            this.title = title;
            this.contents = contents;
            this.url = url;
            this.blogname = blogname;
            this.thumbnail = thumbnail;
            this.datetime = datetime;
        }
    }

    @Data
    @Builder
    public static class RankingResponse {
        private String keyword;
        private Long count;
    }
}
