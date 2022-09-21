package com.razard.search.app.api.blog.service.impl;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogSearchRankingServiceImpl implements BlogSearchRankingService {

    @Value("${redis-key.ranking.search.blog}")
    private String searchRedisKey;

    private final RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> zSetOperations;

    @PostConstruct
    public void init() {
        zSetOperations = redisTemplate.opsForZSet();
    }

    @Override
    public List<BlogDto.RankingResponse> getBlogSearchRanking() {

        Set<ZSetOperations.TypedTuple<String>> rankingSet = zSetOperations.reverseRangeWithScores(searchRedisKey, 0, 9);
        if (ObjectUtils.isEmpty(rankingSet)) {
            return Collections.emptyList();
        }
        return rankingSet.stream().map(item -> BlogDto.RankingResponse.builder()
                .keyword(item.getValue())
                .count(ObjectUtils.isEmpty(item.getScore()) ? 0 : item.getScore().longValue())
                .build()).collect(Collectors.toList());
    }

    @Override
    public void incrementBlogSearchScore(final String query) {
        zSetOperations.incrementScore(searchRedisKey, query, 1);
    }
}
