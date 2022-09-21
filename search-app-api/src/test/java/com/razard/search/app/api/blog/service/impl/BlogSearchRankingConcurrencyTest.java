package com.razard.search.app.api.blog.service.impl;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.config.EmbeddedRedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@DataRedisTest
@Import({EmbeddedRedisConfig.class, BlogSearchRankingServiceImpl.class})
public class BlogSearchRankingConcurrencyTest {

    @Autowired
    BlogSearchRankingServiceImpl blogSearchRankingService;

    @DisplayName("검색어 조회수 증가 동시성 테스트")
    @Test
    void testConcurrency() throws InterruptedException {
        //given
        int concurrency = 100;
        String query = "스프링";
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(concurrency);

        //when
        for (int i = 0; i < concurrency; i++) {
            executorService.execute(() -> {
                blogSearchRankingService.incrementBlogSearchScore(query);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        List<BlogDto.RankingResponse> blogSearchRanking = blogSearchRankingService.getBlogSearchRanking();
        BlogDto.RankingResponse rankingResponse = blogSearchRanking.get(0);

        //then
        assertThat(rankingResponse.getCount()).isEqualTo(concurrency);
        assertThat(rankingResponse.getKeyword()).isEqualTo(query);
    }
}
