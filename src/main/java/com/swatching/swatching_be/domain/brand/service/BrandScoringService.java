package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class BrandScoringService {

    // ── 세부 점수 ──────────────────────────────────────────────────

    /** 등록 최신도: 0~100 */
    double recency(LocalDateTime createdAt) {
        if (createdAt == null) return 10;
        long days = ChronoUnit.DAYS.between(createdAt, LocalDateTime.now());
        if (days <= 7)  return 100;
        if (days <= 14) return 80;
        if (days <= 30) return 60;
        if (days <= 60) return 40;
        if (days <= 90) return 20;
        return 10;
    }

    /** 콘텐츠 완성도: 0~100 */
    double completeness(Brand brand, int visualCount) {
        double score = 0;
        if (hasText(brand.getMainImageUrl()))  score += 25;
        if (hasText(brand.getSummary()))       score += 15;
        if (hasText(brand.getStory()))         score += 20;
        if (visualCount >= 4)                  score += 30; // 20 + 10
        else if (visualCount >= 2)             score += 20;
        if (hasText(brand.getInstagramUrl()) || hasText(brand.getWebsiteUrl())) score += 10;
        return score;
    }

    /** 무드 매칭도: 0~100 */
    double moodMatch(List<String> brandKeywords, List<String> selectedKeywords) {
        if (selectedKeywords.isEmpty()) return 0;
        long matched = brandKeywords.stream().filter(selectedKeywords::contains).count();
        return (double) matched / selectedKeywords.size() * 100;
    }

    /**
     * 다양성 보정 계수: 0.0 / 0.5 / 1.0
     * recentMoods: 결과 리스트에 이미 담긴 브랜드들의 대표 무드 (최근 2개만 사용)
     */
    double diversityFactor(String currentMood, List<String> recentMoods) {
        if (recentMoods.isEmpty() || currentMood.isBlank()) return 1.0;
        int size = recentMoods.size();
        String prev1 = recentMoods.get(size - 1);
        if (!currentMood.equals(prev1)) return 1.0;
        if (size >= 2 && currentMood.equals(recentMoods.get(size - 2))) return 0.0;
        return 0.5;
    }

    // ── 상황별 최종 점수 ──────────────────────────────────────────

    /** Home 전체 보기 — 다양성 보정 제외한 기본 점수 */
    double homeAllBase(Brand brand, int visualCount, boolean isSaved) {
        return recency(brand.getCreatedAt()) * 0.40
                + completeness(brand, visualCount) * 0.35
                - (isSaved ? 15 : 0);
    }

    /** Home 전체 보기 — 다양성 포함 최종 점수 */
    double homeAllFull(Brand brand, int visualCount, boolean isSaved,
                       double diversityFactor) {
        return homeAllBase(brand, visualCount, isSaved) + diversityFactor * 25;
    }

    /** Home 무드 선택 — 다양성 보정 없음 */
    double homeMood(Brand brand, List<String> brandKeywords, int visualCount,
                    List<String> selectedKeywords, boolean isSaved) {
        return moodMatch(brandKeywords, selectedKeywords) * 0.60
                + completeness(brand, visualCount) * 0.25
                + recency(brand.getCreatedAt()) * 0.15
                - (isSaved ? 15 : 0);
    }

    /** Board 덱 — 다양성 보정 제외한 기본 점수 (저장 브랜드는 호출 전에 제거) */
    double boardBase(Brand brand, int visualCount) {
        return completeness(brand, visualCount) * 0.45
                + recency(brand.getCreatedAt()) * 0.35;
    }

    /** Board 덱 — 다양성 포함 최종 점수 */
    double boardFull(Brand brand, int visualCount, double diversityFactor) {
        return boardBase(brand, visualCount) + diversityFactor * 20;
    }

    // ─────────────────────────────────────────────────────────────

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
