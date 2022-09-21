package com.razard.search.app.api.blog.service.impl;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.dto.KakaoBlogsDto;
import com.razard.search.app.api.blog.dto.NaverBlogDto;
import com.razard.search.app.api.blog.dto.NaverBlogsDto;
import com.razard.search.app.api.blog.repository.KakaoApiRepository;
import com.razard.search.app.api.blog.repository.NaverApiRepository;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import com.razard.search.app.api.blog.service.BlogSearchService;
import com.razard.search.app.api.config.EmbeddedRedisConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.collection.Stream;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BlogSearchCircuitBreakerTest {

    @Autowired
    private BlogSearchService blogSearchService;

    @Autowired
    protected CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    EmbeddedRedisConfig embeddedRedisConfig;

    @MockBean
    private BlogSearchRankingService blogSearchRankingService;

    @MockBean
    private KakaoApiRepository kakaoApiRepository;

    @MockBean
    private NaverApiRepository naverApiRepository;

    @Test
    @DisplayName("카카오 검색 장애 발생 시 서킷 오픈 후, 네이버 검색 으로 대응")
    void test_kakao_search_exception_circuit_open_and_naver_search() throws Exception {

        //given
        BlogDto.SearchRequest request = BlogDto.SearchRequest.builder()
                .query("spring").sort("accuracy").page(1).size(10).build();

        Call<KakaoBlogsDto> kakaoBlogsDtoCall = mock(Call.class);
        Response<KakaoBlogsDto> kakaoBlogsDtoResponse = Response.error(500,
                ResponseBody.create(okhttp3.MediaType.parse(MediaType.APPLICATION_JSON_VALUE), "content"));

        when(kakaoApiRepository.searchBlogs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(kakaoBlogsDtoCall);
        when(kakaoBlogsDtoCall.execute()).thenReturn(kakaoBlogsDtoResponse);

        Call<NaverBlogsDto> naverBlogsDtoCall = mock(Call.class);
        Response<NaverBlogsDto> naverBlogsDtoResponse = Response.success(NaverBlogsDto.builder()
                .items(List.of(NaverBlogDto.builder().bloggername("블로거").title("블로거제목").build())).total(10)
                .build());

        when(naverApiRepository.searchBlogs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(naverBlogsDtoCall);
        when(naverBlogsDtoCall.execute()).thenReturn(naverBlogsDtoResponse);

        //when
        Stream.range(0, 4).forEach(c -> blogSearchService.searchBlogs(request));
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(BlogSearchServiceImpl.CB_SEARCH_SOURCE_REPOSITORY);
        Page<BlogDto.SearchResponse> result = blogSearchService.searchBlogs(request);

        //then
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getContent().get(0).getBlogname()).isEqualTo("블로거");
        verify(naverApiRepository, times(5)).searchBlogs(anyString(), anyString(), anyInt(), anyInt());
        verify(blogSearchRankingService, times(5)).incrementBlogSearchScore(anyString());
    }
}
