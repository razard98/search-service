package com.razard.search.app.api.blog.service.impl;

import com.razard.search.domain.blog.BlogSearchLog;
import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.dto.KakaoBlogsDto;
import com.razard.search.app.api.blog.dto.NaverBlogsDto;
import com.razard.search.app.api.blog.repository.KakaoApiRepository;
import com.razard.search.app.api.blog.repository.NaverApiRepository;
import com.razard.search.app.api.blog.service.BlogSearchLogService;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import com.razard.search.app.api.blog.service.BlogSearchService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Response;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogSearchServiceImpl implements BlogSearchService {

    private final static String SORT_ACCURACY = "accuracy";
    private final KakaoApiRepository kakaoApiRepository;
    private final NaverApiRepository naverApiRepository;
    private final BlogSearchRankingService blogSearchRankingService;
    private final BlogSearchLogService blogSearchLogService;

    @Override
    @CircuitBreaker(name = "search-source", fallbackMethod = "searchBlogsFallback")
    public Page<BlogDto.SearchResponse> searchBlogs(final BlogDto.SearchRequest request) {

        log.debug("Call kakao api");
        Response<KakaoBlogsDto> result;
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        try {

            result = kakaoApiRepository.searchBlogs(request.getQuery(), request.getSort(), request.getPage(), request.getSize())
                    .execute();

            if (ObjectUtils.isEmpty(result.body()) || !result.isSuccessful()) {
                assert result.errorBody() != null;
                String errorMessage = result.errorBody().string();
                throw new RuntimeException(errorMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        long totalCount = result.body().getMeta().getPageableCount();
        List<BlogDto.SearchResponse> list = result.body()
                .getDocuments()
                .stream()
                .map(item -> BlogDto.SearchResponse.builder()
                        .title(item.getTitle())
                        .contents(item.getBlogname())
                        .url(item.getUrl())
                        .blogname(item.getBlogname())
                        .thumbnail(item.getThumbnail())
                        .datetime(item.getDatetime())
                        .build())
                .collect(Collectors.toList());

        postProcess(request.getQuery(), BlogSearchLog.Source.KAKAO);

        return new PageImpl<>(list, pageable, totalCount);
    }

    private Page<BlogDto.SearchResponse> searchBlogsFallback(final BlogDto.SearchRequest request, Throwable t) {

        log.debug("=============[CircuitBreaker Open] Call Naver api");
        Response<NaverBlogsDto> result;
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        int start = (request.getPage() - 1) * request.getPage() + 1;
        String sort = SORT_ACCURACY.equals(request.getSort()) ? "sim" : "date";

        try {

            result = naverApiRepository.searchBlogs(request.getQuery(), sort, start, request.getSize()).execute();

            if (ObjectUtils.isEmpty(result.body()) || !result.isSuccessful()) {
                assert result.errorBody() != null;
                String errorMessage = result.errorBody().string();
                throw new RuntimeException(errorMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        long totalCount = result.body().getTotal();
        List<BlogDto.SearchResponse> list = result.body()
                .getItems()
                .stream()
                .map(item -> BlogDto.SearchResponse.builder()
                        .title(item.getTitle())
                        .contents(item.getDescription())
                        .url(item.getBloggerlink())
                        .blogname(item.getBloggername())
                        .datetime(item.getPostdate())
                        .build())
                .collect(Collectors.toList());

        postProcess(request.getQuery(), BlogSearchLog.Source.NAVER);

        return new PageImpl<>(list, pageable, totalCount);
    }

    private void postProcess(final String query, final BlogSearchLog.Source source) {
        blogSearchRankingService.incrementBlogSearchScore(query);
        blogSearchLogService.save(BlogSearchLog.builder().query(query).source(source).searchTime(LocalDateTime.now()).build());
    }
}
