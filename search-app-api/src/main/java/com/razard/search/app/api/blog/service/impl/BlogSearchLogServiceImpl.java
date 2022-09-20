package com.razard.search.app.api.blog.service.impl;

import com.razard.search.domain.blog.BlogSearchLog;
import com.razard.search.app.api.blog.dto.BlogSearchLogDto;
import com.razard.search.app.api.blog.repository.BlogSearchLogRepository;
import com.razard.search.app.api.blog.service.BlogSearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogSearchLogServiceImpl implements BlogSearchLogService {

    private final BlogSearchLogRepository blogSearchLogRepository;

    @Override
    public void save(BlogSearchLog blogSearchLog) {
        blogSearchLogRepository.save(blogSearchLog);
    }

    @Override
    public Page<BlogSearchLogDto> getBlogSearchLogs(final Integer page, final Integer size) {

        Page<BlogSearchLog> result = blogSearchLogRepository.findAllByOrderByIdDesc(PageRequest.of(page - 1, size));
        List<BlogSearchLogDto> collect = result.stream().map(this::entityToDto).collect(Collectors.toList());

        return new PageImpl<>(collect, result.getPageable(), result.getTotalElements());
    }

    private BlogSearchLogDto entityToDto(final BlogSearchLog blogSearchLog) {
        return BlogSearchLogDto.builder()
                .id(blogSearchLog.getId())
                .query(blogSearchLog.getQuery())
                .source(blogSearchLog.getSource().name())
                .searchTime(blogSearchLog.getSearchTime())
                .build();
    }
}
