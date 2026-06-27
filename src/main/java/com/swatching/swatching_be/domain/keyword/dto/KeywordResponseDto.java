package com.swatching.swatching_be.domain.keyword.dto;

import com.swatching.swatching_be.domain.keyword.Keyword;
import lombok.Getter;

@Getter
public class KeywordResponseDto {

    private final Long keywordId;
    private final String name;

    public KeywordResponseDto(Keyword keyword) {
        this.keywordId = keyword.getId();
        this.name = keyword.getName();
    }
}
