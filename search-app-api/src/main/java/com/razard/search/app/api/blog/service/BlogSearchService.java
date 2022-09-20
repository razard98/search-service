package com.razard.search.app.api.blog.service;

import com.razard.search.app.api.blog.dto.BlogDto;
import org.springframework.data.domain.Page;


public interface BlogSearchService {

    Page<BlogDto.SearchResponse> searchBlogs(final BlogDto.SearchRequest request);
}
