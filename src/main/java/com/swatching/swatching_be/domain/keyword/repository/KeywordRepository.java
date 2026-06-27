package com.swatching.swatching_be.domain.keyword.repository;

import com.swatching.swatching_be.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}