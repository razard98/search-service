package com.razard.search.app.api.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KakaoBlogMetaDto {

    @JsonProperty("total_count")
    private Integer totalCount;             //검색된 문서 수

    @JsonProperty("pageable_count")
    private Integer pageableCount;          //total_count 중 노출 가능 문서 수

    @JsonProperty("is_end")
    private Boolean isEnd;                  //현재 페이지가 마지막 페이지인지 여부

    @Builder
    public KakaoBlogMetaDto(Integer totalCount, Integer pageableCount, Boolean isEnd) {
        this.totalCount = totalCount;
        this.pageableCount = pageableCount;
        this.isEnd = isEnd;
    }
}
