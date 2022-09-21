package com.razard.search.app.api.blog.controller;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.dto.BlogSearchLogDto;
import com.razard.search.app.api.blog.service.BlogSearchLogService;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import com.razard.search.app.api.blog.service.BlogSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RequestMapping("/api/v1/search")
@RestController
@Validated
@RequiredArgsConstructor
public class BlogSearchController {

    private final BlogSearchService blogSearchService;
    private final BlogSearchLogService blogSearchLogService;
    private final BlogSearchRankingService blogSearchRankingService;

    @GetMapping("/blog")
    @ResponseBody
    public ResponseEntity<Page<BlogDto.SearchResponse>> searchBlogs(
            @RequestParam("query") @NotBlank(message = "검색어는 필수 입니다.") String query,
            @RequestParam(value = "sort", defaultValue = "accuracy") String sort,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {

        BlogDto.SearchRequest request = BlogDto.SearchRequest.builder()
                .query(query).sort(sort).page(page).size(size).build();
        return new ResponseEntity<>(blogSearchService.searchBlogs(request), HttpStatus.OK);
    }

    @GetMapping("/blog/rankings")
    @ResponseBody
    public ResponseEntity<List<BlogDto.RankingResponse>> getBlogSearchRanking() {

        return new ResponseEntity<>(blogSearchRankingService.getBlogSearchRanking(), HttpStatus.OK);
    }

    @GetMapping("/blog/logs")
    public ResponseEntity<Page<BlogSearchLogDto>> getBlogSearchLog(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ResponseEntity<>(blogSearchLogService.getBlogSearchLogs(page, size), HttpStatus.OK);
    }
}
