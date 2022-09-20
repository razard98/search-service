package com.razard.search.app.api.blog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class KakaoBlogsDto {

    @JsonProperty("meta")
    private KakaoBlogMetaDto meta;

    @JsonProperty("documents")
    private List<KakaoBlogDocumentDto> documents;

    @Builder
    public KakaoBlogsDto(KakaoBlogMetaDto meta, List<KakaoBlogDocumentDto> documents) {
        this.meta = meta;
        this.documents = documents;
    }
}
