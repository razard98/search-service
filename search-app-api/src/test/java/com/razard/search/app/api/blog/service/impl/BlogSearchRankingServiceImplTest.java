package com.razard.search.app.api.blog.service.impl;

import com.razard.search.app.api.blog.dto.BlogDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogSearchRankingServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private BlogSearchRankingServiceImpl blogSearchRankingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(blogSearchRankingService, "searchRedisKey", "key");
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        blogSearchRankingService.init();
    }

    @Test
    @DisplayName("블로그 검색어 회수 증가 성공")
    void test_increment_blog_search_score_success() {

        //when
        blogSearchRankingService.incrementBlogSearchScore("spring");

        //then
        verify(zSetOperations, times(1)).incrementScore(anyString(), anyString(), anyDouble());

    }

    @Test
    @DisplayName("블로그 검색어 회수 목록 가져오기 성공")
    void test_get_blog_search_ranking_success() {

        //given
        Set<ZSetOperations.TypedTuple<String>> rankingMock = mock(Set.class);
        when(zSetOperations.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(rankingMock);

        //when
        List<BlogDto.RankingResponse> blogSearchRankings = blogSearchRankingService.getBlogSearchRanking();

        //then
        assertThat(blogSearchRankings).isNotNull();
        assertThat(blogSearchRankings.size()).isEqualTo(0);

    }
}