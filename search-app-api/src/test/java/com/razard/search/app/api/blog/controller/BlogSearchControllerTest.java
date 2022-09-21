package com.razard.search.app.api.blog.controller;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.dto.BlogSearchLogDto;
import com.razard.search.app.api.blog.service.BlogSearchLogService;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import com.razard.search.app.api.blog.service.BlogSearchService;
import com.razard.search.domain.blog.BlogSearchLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BlogSearchController.class)
@AutoConfigureRestDocs
class BlogSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BlogSearchService blogSearchService;

    @MockBean
    private BlogSearchLogService blogSearchLogService;

    @MockBean
    private BlogSearchRankingService blogSearchRankingService;

    @Test
    @DisplayName("블로그 검색 페이징 성공")
    void test_blog_search_paging_success() throws Exception {

        //given
        BlogDto.SearchRequest request = BlogDto.SearchRequest.builder()
                .query("스프링").page(1).size(10).sort("accuracy").build();
        List<BlogDto.SearchResponse> list = List.of(
                BlogDto.SearchResponse.builder().title("스프링1").blogname("블로그1").build(),
                BlogDto.SearchResponse.builder().title("스프링2").blogname("블로그2").build(),
                BlogDto.SearchResponse.builder().title("스프링3").blogname("블로그3").build(),
                BlogDto.SearchResponse.builder().title("스프링4").blogname("블로그4").build(),
                BlogDto.SearchResponse.builder().title("스프링5").blogname("블로그5").build()
        );
        Page<BlogDto.SearchResponse> result = new PageImpl<>(list, PageRequest.of(0, 10), 10);

        given(blogSearchService.searchBlogs(request)).willReturn(result);

        //when-then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/search/blog")
                .param("query", request.getQuery())
                .param("page", request.getPage().toString())
                .param("size", request.getSize().toString())
                .param("sort", request.getSort())
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("blog-search",
                        requestParameters(
                                parameterWithName("query").description("검색어"),
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("사이즈"),
                                parameterWithName("sort").description("정렬(accuracy : 정확도, recency: 최신순)")
                        )
                ))
                .andExpect(jsonPath("$.content[0].title", is(notNullValue())));
    }

    @Test
    @DisplayName("블로그 검색 랭킹 조회 성공")
    void test_blog_search_ranking_success() throws Exception {
        //given
        List<BlogDto.RankingResponse> results = List.of(
                BlogDto.RankingResponse.builder().keyword("Spring").count(10L).build(),
                BlogDto.RankingResponse.builder().keyword("JPA").count(5L).build(),
                BlogDto.RankingResponse.builder().keyword("MSA").count(1L).build()
        );
        given(blogSearchRankingService.getBlogSearchRanking()).willReturn(results);

        //when-then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/search/blog/rankings")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("blog-search-ranking",
                        responseFields(
                                fieldWithPath("[].keyword").type(JsonFieldType.STRING).description("검색어"),
                                fieldWithPath("[].count").type(JsonFieldType.NUMBER).description("조회수")
                        )
                ))
                .andExpect(jsonPath("$[0].keyword", is(notNullValue())))
                .andExpect(jsonPath("$[0].count", is(notNullValue())));
    }

    @Test
    @DisplayName("블로그 검색 로그 페이징 성공")
    void test_blog_search_log_paging_success() throws Exception {

        //given
        int page = 1, size = 10, total = 10;
        List<BlogSearchLogDto> list =
                List.of(BlogSearchLogDto.builder()
                        .id(1L).query("스프링").searchTime(LocalDateTime.now())
                        .source(BlogSearchLog.Source.KAKAO.name())
                        .build());
        Page<BlogSearchLogDto> result = new PageImpl<>(list, PageRequest.of(page - 1, size), total);

        given(blogSearchLogService.getBlogSearchLogs(page, size)).willReturn(result);

        //when-then
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/search/blog/logs")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("blog-search-log",
                        requestParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("사이즈")
                        )
                ))
                .andExpect(jsonPath("$.content[0].query", is(notNullValue())));
    }
}