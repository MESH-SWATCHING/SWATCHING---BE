package com.swatching.swatching_be.domain.keyword.service;

import com.swatching.swatching_be.domain.keyword.dto.KeywordResponseDto;
import com.swatching.swatching_be.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public List<KeywordResponseDto> getAllKeywords() {
        return keywordRepository.findAll().stream()
                .map(KeywordResponseDto::new)
                .collect(Collectors.toList());
    }
}
