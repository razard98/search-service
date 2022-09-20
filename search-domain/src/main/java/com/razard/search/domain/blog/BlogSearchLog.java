package com.razard.search.domain.blog;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blog_search_log")
public class BlogSearchLog implements Serializable {

    public enum Source {KAKAO, NAVER}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "query")
    private String query;

    @Column(name = "search_time")
    private LocalDateTime searchTime;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Builder
    public BlogSearchLog(String query, LocalDateTime searchTime, Source source) {
        this.query = query;
        this.searchTime = searchTime;
        this.source = source;
    }
}

