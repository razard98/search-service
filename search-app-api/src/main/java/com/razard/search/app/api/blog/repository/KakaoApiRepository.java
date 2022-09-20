package com.razard.search.app.api.blog.repository;

import com.razard.search.app.api.blog.dto.KakaoBlogsDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KakaoApiRepository {

    @GET("/v2/search/blog")
    Call<KakaoBlogsDto> searchBlogs(@Query("query") String query, @Query("sort") String sort, @Query("page") Integer page,
                                    @Query("size") Integer size);

}
