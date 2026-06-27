package com.swatching.swatching_be.domain.keyword.controller;

import com.swatching.swatching_be.domain.keyword.dto.KeywordResponseDto;
import com.swatching.swatching_be.domain.keyword.service.KeywordService;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/keywords")
    public ResponseEntity<ApiResponse<List<KeywordResponseDto>>> getKeywords() {
        List<KeywordResponseDto> data = keywordService.getAllKeywords();
        return ResponseEntity.ok(ApiResponse.success("무드 목록 조회가 완료되었습니다.", data));
    }
}
