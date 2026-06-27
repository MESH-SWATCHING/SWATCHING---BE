package com.swatching.swatching_be.domain.keyword.repository;

import com.swatching.swatching_be.domain.keyword.Keyword;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByNameIn(Collection<String> names);
}
