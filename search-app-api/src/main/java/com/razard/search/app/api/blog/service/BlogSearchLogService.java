package com.razard.search.app.api.blog.service;

import com.razard.search.domain.blog.BlogSearchLog;
import com.razard.search.app.api.blog.dto.BlogSearchLogDto;
import org.springframework.data.domain.Page;

public interface BlogSearchLogService {

    void save(final BlogSearchLog blogSearchLog);

    Page<BlogSearchLogDto> getBlogSearchLogs(final Integer page, final Integer size);
}
