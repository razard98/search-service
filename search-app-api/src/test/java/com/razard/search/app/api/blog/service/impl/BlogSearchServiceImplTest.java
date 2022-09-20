package com.razard.search.app.api.blog.service.impl;

import com.razard.search.app.api.blog.dto.BlogDto;
import com.razard.search.app.api.blog.dto.KakaoBlogDocumentDto;
import com.razard.search.app.api.blog.dto.KakaoBlogMetaDto;
import com.razard.search.app.api.blog.dto.KakaoBlogsDto;
import com.razard.search.app.api.blog.repository.KakaoApiRepository;
import com.razard.search.app.api.blog.service.BlogSearchLogService;
import com.razard.search.app.api.blog.service.BlogSearchRankingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import retrofit2.Call;
import retrofit2.Response;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogSearchServiceImplTest {

    @InjectMocks
    private BlogSearchServiceImpl blogSearchService;

    @Mock
    private BlogSearchRankingService blogSearchRankingService;

    @Mock
    private BlogSearchLogService blogSearchLogService;

    @Mock
    private KakaoApiRepository kakaoApiRepository;

    @Test
    @DisplayName("블로그 검색 성공")
    void test_blog_search_is_success() throws Exception {

        //given
        BlogDto.SearchRequest request = BlogDto.SearchRequest.builder()
                .query("spring").sort("accuracy").page(1).size(10).build();

        KakaoBlogsDto response = KakaoBlogsDto.builder()
                .documents(List.of(KakaoBlogDocumentDto.builder().blogname("블로그").title("제목").contents("내용").datetime("").build()))
                .meta(KakaoBlogMetaDto.builder().pageableCount(10).totalCount(100).isEnd(false).build())
                .build();

        Call<KakaoBlogsDto> kakaoBlogsDtoCall = mock(Call.class);
        Response<KakaoBlogsDto> kakaoBlogsDtoResponse = Response.success(response);

        when(kakaoApiRepository.searchBlogs(anyString(), anyString(), anyInt(), anyInt())).thenReturn(kakaoBlogsDtoCall);
        when(kakaoBlogsDtoCall.execute()).thenReturn(kakaoBlogsDtoResponse);

        //when
        Page<BlogDto.SearchResponse> result = blogSearchService.searchBlogs(request);

        //then
        assertThat(result.getContent().get(0).getBlogname()).isEqualTo("블로그");
        verify(kakaoApiRepository, times(1)).searchBlogs(anyString(), anyString(), anyInt(), anyInt());
        verify(blogSearchRankingService, times(1)).incrementBlogSearchScore(anyString());
        verify(blogSearchLogService, times(1)).save(any());
    }

}