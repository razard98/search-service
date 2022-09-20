package com.razard.search.app.api.blog.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BlogSearchLogDto {

    private Long id;

    private String query;

    private LocalDateTime searchTime;
    
    private String source;


}
