package com.razard.search.app.api.blog.service;

import com.razard.search.app.api.blog.dto.BlogDto;

import java.util.List;

public interface BlogSearchRankingService {

    List<BlogDto.RankingResponse> getBlogSearchRanking();

    void incrementBlogSearchScore(final String query);

}
