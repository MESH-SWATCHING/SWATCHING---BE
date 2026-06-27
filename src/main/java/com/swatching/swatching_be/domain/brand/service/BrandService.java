package com.swatching.swatching_be.domain.brand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandImage;
import com.swatching.swatching_be.domain.brand.BrandVisibility;
import com.swatching.swatching_be.domain.brand.BrandKeyword;
import com.swatching.swatching_be.domain.brand.dto.BrandCardDto;
import com.swatching.swatching_be.domain.brand.dto.BrandDeckResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandDetailResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandRecommendResponse;
import com.swatching.swatching_be.domain.brand.dto.BrandResponseDto;
import com.swatching.swatching_be.domain.brand.dto.BrandSubmitRequest;
import com.swatching.swatching_be.domain.brand.repository.BrandImageRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandKeywordRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import com.swatching.swatching_be.domain.image.service.ImageUploadService;
import com.swatching.swatching_be.domain.savedbrand.repository.SavedBrandRepository;
import com.swatching.swatching_be.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {

    private final BrandRepository brandRepository;
    private final BrandKeywordRepository brandKeywordRepository;
    private final BrandImageRepository brandImageRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final BrandScoringService scoring;
    private final ImageUploadService imageUploadService;

    // ── Home 전체 보기 / 무드 선택 ───────────────────────────────────

    public List<BrandResponseDto> getBrands(String keywordsParam, User user) {
        List<Brand> brands = fetchBrands(keywordsParam);
        boolean isMoodFilter = isMoodFilter(keywordsParam);
        List<String> selectedKeywords = isMoodFilter
                ? Arrays.asList(keywordsParam.split(","))
                : List.of();

        Map<Long, List<String>> keywordMap = buildKeywordMap(brands);
        Map<Long, List<String>> imageMap = buildImageMap(brands);
        Set<Long> savedIds = savedBrandRepository.findSavedBrandIdsByUserId(user.getId());

        List<BrandCtx> contexts = brands.stream()
                .map(b -> new BrandCtx(b, keywordMap.getOrDefault(b.getId(), List.of()),
                        imageMap.getOrDefault(b.getId(), List.of())))
                .collect(Collectors.toList());

        List<BrandCtx> sorted = isMoodFilter
                ? sortByMood(contexts, selectedKeywords, savedIds)
                : sortByHomeAll(contexts, savedIds);

        return sorted.stream()
                .map(ctx -> new BrandResponseDto(
                        ctx.brand(),
                        ctx.keywords(),
                        imageUploadService.createViewUrl(ctx.brand().getMainImageUrl())
                ))
                .collect(Collectors.toList());
    }

    // ── Board 덱 ─────────────────────────────────────────────────────

    public BrandDeckResponseDto getBrandDeck(String keywordsParam, User user) {
        List<Brand> brands = fetchBrands(keywordsParam);
        boolean isMoodFilter = isMoodFilter(keywordsParam);
        List<String> selectedKeywords = isMoodFilter
                ? Arrays.asList(keywordsParam.split(","))
                : List.of();

        Map<Long, List<String>> keywordMap = buildKeywordMap(brands);
        Map<Long, List<String>> imageMap = buildImageMap(brands);
        Set<Long> savedIds = savedBrandRepository.findSavedBrandIdsByUserId(user.getId());

        // Board: 저장 브랜드 완전 제외
        List<BrandCtx> contexts = brands.stream()
                .filter(b -> !savedIds.contains(b.getId()))
                .map(b -> new BrandCtx(b, keywordMap.getOrDefault(b.getId(), List.of()),
                        imageMap.getOrDefault(b.getId(), List.of())))
                .collect(Collectors.toList());

        List<BrandCtx> sorted = isMoodFilter
                ? sortByMoodBoard(contexts, selectedKeywords)
                : sortByBoard(contexts);

        List<BrandCardDto> cards = sorted.stream()
                .map(ctx -> new BrandCardDto(
                        ctx.brand(),
                        ctx.keywords(),
                        imageUploadService.createViewUrl(ctx.brand().getMainImageUrl()),
                        imageUploadService.createViewUrls(ctx.images())
                ))
                .collect(Collectors.toList());

        return new BrandDeckResponseDto(cards);
    }

    // ── 브랜드 상세 / 추천 ────────────────────────────────────────────

    public BrandDetailResponse getBrandDetail(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        if (brand.getVisibility() != BrandVisibility.PUBLIC) {
            throw new IllegalArgumentException("접근할 수 없는 브랜드입니다.");
        }

        List<String> visuals = brandImageRepository.findByBrand_Id(brandId).stream()
                .map(BrandImage::getImageUrl).toList();
        List<String> keywords = brandKeywordRepository.findByBrand_Id(brandId).stream()
                .map(bk -> bk.getKeyword().getName()).toList();

        return new BrandDetailResponse(brand.getId(), brand.getName(), brand.getSummary(),
                brand.getStory(), brand.getStorySummary(), imageUploadService.createViewUrl(brand.getMainImageUrl()),
                brand.getInstagramUrl(), brand.getWebsiteUrl(), keywords, imageUploadService.createViewUrls(visuals));
    }

    @Transactional
    public void submitBrand(BrandSubmitRequest request, User user) {
        Brand brand = Brand.createSubmission(
                request.getName(), request.getSummary(),
                request.getInstagramUrl(), request.getWebsiteUrl(),
                request.getManagerName(), request.getManagerEmail(), request.getManagerPhone(),
                user
        );
        brandRepository.save(brand);
    }

    public List<BrandRecommendResponse> getRecommendedBrands(Long brandId) {
        List<String> myKeywords = brandKeywordRepository.findByBrand_Id(brandId).stream()
                .map(bk -> bk.getKeyword().getName())
                .toList();

        if (myKeywords.isEmpty()) return List.of();

        Set<String> myKeywordSet = Set.copyOf(myKeywords);

        return brandRepository.findBrandsHavingAnyKeyword(myKeywords).stream()
                .filter(b -> !b.getId().equals(brandId))
                .map(b -> {
                    List<String> keywords = brandKeywordRepository.findByBrand_Id(b.getId()).stream()
                            .map(bk -> bk.getKeyword().getName()).toList();
                    long overlap = keywords.stream().filter(myKeywordSet::contains).count();
                    return Map.entry(overlap, new BrandRecommendResponse(b.getId(), b.getName(), b.getSummary(), b.getMainImageUrl(), keywords));
                })
                .sorted(Map.Entry.<Long, BrandRecommendResponse>comparingByKey().reversed())
                .limit(5)
                .map(Map.Entry::getValue)
                .toList();
    }

    // ── 정렬 로직 ─────────────────────────────────────────────────────

    /** Home 전체 보기: 최신도(40) + 완성도(35) + 다양성(25) - 저장감점(15) */
    private List<BrandCtx> sortByHomeAll(List<BrandCtx> items, Set<Long> savedIds) {
        List<BrandCtx> remaining = new ArrayList<>(items);
        remaining.sort(Comparator.comparingDouble(
                ctx -> -scoring.homeAllBase(ctx.brand(), ctx.images().size(), savedIds.contains(ctx.brand().getId()))
        ));

        List<BrandCtx> result = new ArrayList<>();
        List<String> recentMoods = new ArrayList<>();

        while (!remaining.isEmpty()) {
            BrandCtx best = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (BrandCtx ctx : remaining) {
                boolean isSaved = savedIds.contains(ctx.brand().getId());
                double df = scoring.diversityFactor(ctx.primaryMood(), recentMoods);
                double score = scoring.homeAllFull(ctx.brand(), ctx.images().size(), isSaved, df);
                if (score > bestScore) {
                    bestScore = score;
                    best = ctx;
                }
            }

            result.add(best);
            remaining.remove(best);
            addRecentMood(recentMoods, best.primaryMood());
        }
        return result;
    }

    /** Home 무드 선택: 무드매칭(60) + 완성도(25) + 최신도(15) - 저장감점(15) */
    private List<BrandCtx> sortByMood(List<BrandCtx> items, List<String> selectedKeywords, Set<Long> savedIds) {
        return items.stream()
                .sorted(Comparator.comparingDouble((BrandCtx ctx) ->
                        scoring.homeMood(ctx.brand(), ctx.keywords(), ctx.images().size(),
                                selectedKeywords, savedIds.contains(ctx.brand().getId()))
                ).reversed())
                .collect(Collectors.toList());
    }

    /** Board 전체 덱: 완성도(45) + 최신도(35) + 다양성(20), 저장 브랜드 제외 */
    private List<BrandCtx> sortByBoard(List<BrandCtx> items) {
        List<BrandCtx> remaining = new ArrayList<>(items);
        remaining.sort(Comparator.comparingDouble(
                ctx -> -scoring.boardBase(ctx.brand(), ctx.images().size())
        ));

        List<BrandCtx> result = new ArrayList<>();
        List<String> recentMoods = new ArrayList<>();

        while (!remaining.isEmpty()) {
            BrandCtx best = null;
            double bestScore = Double.NEGATIVE_INFINITY;

            for (BrandCtx ctx : remaining) {
                double df = scoring.diversityFactor(ctx.primaryMood(), recentMoods);
                double score = scoring.boardFull(ctx.brand(), ctx.images().size(), df);
                if (score > bestScore) {
                    bestScore = score;
                    best = ctx;
                }
            }

            result.add(best);
            remaining.remove(best);
            addRecentMood(recentMoods, best.primaryMood());
        }
        return result;
    }

    /** Board 무드 선택: 무드매칭(60) + 완성도(25) + 최신도(15), 저장 브랜드 제외 */
    private List<BrandCtx> sortByMoodBoard(List<BrandCtx> items, List<String> selectedKeywords) {
        return items.stream()
                .sorted(Comparator.comparingDouble((BrandCtx ctx) ->
                        scoring.homeMood(ctx.brand(), ctx.keywords(), ctx.images().size(),
                                selectedKeywords, false)
                ).reversed())
                .collect(Collectors.toList());
    }

    // ── 공통 유틸 ─────────────────────────────────────────────────────

    private List<Brand> fetchBrands(String keywordsParam) {
        if (!isMoodFilter(keywordsParam)) return brandRepository.findAllByVisibility(BrandVisibility.PUBLIC);
        return brandRepository.findBrandsHavingAnyKeyword(Arrays.asList(keywordsParam.split(",")));
    }

    private boolean isMoodFilter(String keywordsParam) {
        return keywordsParam != null && !keywordsParam.isBlank()
                && !keywordsParam.equalsIgnoreCase("all");
    }

    private Map<Long, List<String>> buildKeywordMap(List<Brand> brands) {
        return brandKeywordRepository.findByBrandIn(brands).stream()
                .collect(Collectors.groupingBy(
                        bk -> bk.getBrand().getId(),
                        Collectors.mapping(bk -> bk.getKeyword().getName(), Collectors.toList())
                ));
    }

    private Map<Long, List<String>> buildImageMap(List<Brand> brands) {
        return brandImageRepository.findByBrandIn(brands).stream()
                .collect(Collectors.groupingBy(
                        bi -> bi.getBrand().getId(),
                        Collectors.mapping(BrandImage::getImageUrl, Collectors.toList())
                ));
    }

    private void addRecentMood(List<String> recentMoods, String mood) {
        recentMoods.add(mood);
        if (recentMoods.size() > 2) recentMoods.remove(0);
    }

    // ── 내부 컨텍스트 레코드 ──────────────────────────────────────────

    private record BrandCtx(Brand brand, List<String> keywords, List<String> images) {
        String primaryMood() {
            return keywords.isEmpty() ? "" : keywords.get(0);
        }
    }
}
