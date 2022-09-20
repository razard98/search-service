package com.razard.search.app.api.blog.repository;

import com.razard.search.domain.blog.BlogSearchLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogSearchLogRepository extends JpaRepository<BlogSearchLog, Long> {

    Page<BlogSearchLog> findAllByOrderByIdDesc(Pageable pageable);

}
