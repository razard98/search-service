package com.razard.search.app.api.blog.repository;

import com.razard.search.app.api.blog.dto.NaverBlogsDto;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NaverApiRepository {

    @GET("/v1/search/blog.json")
    Call<NaverBlogsDto> searchBlogs(@Query("query") String query, @Query("sort") String sort, @Query("start") Integer start,
                                    @Query("display") Integer display);

}
