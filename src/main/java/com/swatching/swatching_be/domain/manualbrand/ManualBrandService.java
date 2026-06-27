package com.swatching.swatching_be.domain.manualbrand;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import com.swatching.swatching_be.domain.category.Category;
import com.swatching.swatching_be.domain.category.CategoryRepository;
import com.swatching.swatching_be.domain.category.CategoryService;
import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategory;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandRepository;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ManualBrandService {

    private final BrandRepository brandRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final SavedBrandCategoryRepository savedBrandCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public ManualBrandService(
            BrandRepository brandRepository,
            SavedBrandRepository savedBrandRepository,
            SavedBrandCategoryRepository savedBrandCategoryRepository,
            CategoryRepository categoryRepository,
            CategoryService categoryService
    ) {
        this.brandRepository = brandRepository;
        this.savedBrandRepository = savedBrandRepository;
        this.savedBrandCategoryRepository = savedBrandCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public ManualBrandResponse createManualBrand(User user, CreateManualBrandRequest request) {
        String name = normalizeRequiredText(request.name(), "브랜드 이름은 필수입니다.", 255);
        String instagramUrl = normalizeUrl(request.instagramUrl(), "instagramUrl");
        String websiteUrl = normalizeUrl(request.websiteUrl(), "websiteUrl");
        String memo = normalizeOptionalText(request.memo(), "메모는 최대 500자까지 가능합니다.", 500);

        Category defaultCategory = categoryService.getOrCreateDefaultCategory(user);
        List<Category> categories = resolveCategories(user, defaultCategory, request.categoryIds());

        Brand brand = brandRepository.save(Brand.createManual(name, instagramUrl, websiteUrl, user));
        SavedBrand savedBrand = savedBrandRepository.save(SavedBrand.create(user, brand, memo));

        List<SavedBrandCategory> links = categories.stream()
                .map(category -> SavedBrandCategory.create(savedBrand, category))
                .toList();
        savedBrandCategoryRepository.saveAll(links);

        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .toList();
        return ManualBrandResponse.of(savedBrand.getId(), brand, categoryIds);
    }

    private List<Category> resolveCategories(User user, Category defaultCategory, List<Long> requestedCategoryIds) {
        LinkedHashSet<Long> orderedCategoryIds = new LinkedHashSet<>();
        orderedCategoryIds.add(defaultCategory.getId());

        if (requestedCategoryIds != null) {
            requestedCategoryIds.stream()
                    .filter(categoryId -> categoryId != null)
                    .forEach(orderedCategoryIds::add);
        }

        Set<Long> requestedWithoutDefault = new HashSet<>(orderedCategoryIds);
        requestedWithoutDefault.remove(defaultCategory.getId());

        List<Category> selectedCategories = requestedWithoutDefault.isEmpty()
                ? List.of()
                : categoryRepository.findAllByIdInAndUserId(requestedWithoutDefault, user.getId());

        Set<Long> foundIds = selectedCategories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());
        if (!foundIds.containsAll(requestedWithoutDefault)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "카테고리를 찾을 수 없습니다.");
        }

        List<Category> result = new ArrayList<>();
        result.add(defaultCategory);
        orderedCategoryIds.stream()
                .filter(categoryId -> !categoryId.equals(defaultCategory.getId()))
                .forEach(categoryId -> selectedCategories.stream()
                        .filter(category -> category.getId().equals(categoryId))
                        .findFirst()
                        .ifPresent(result::add));
        return result;
    }

    private String normalizeRequiredText(String value, String blankMessage, int maxLength) {
        String normalized = value == null ? "" : value.trim();
        if (normalized.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, blankMessage);
        }
        if (normalized.length() > maxLength) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "브랜드 이름은 최대 255자까지 가능합니다.");
        }
        return normalized;
    }

    private String normalizeOptionalText(String value, String tooLongMessage, int maxLength) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isBlank()) {
            return null;
        }
        if (normalized.length() > maxLength) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, tooLongMessage);
        }
        return normalized;
    }

    private String normalizeUrl(String value, String fieldName) {
        String normalized = normalizeOptionalText(value, fieldName + "은 너무 깁니다.", 2048);
        if (normalized == null) {
            return null;
        }

        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + "은 http 또는 https URL이어야 합니다.");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " 형식이 올바르지 않습니다.");
            }
            return normalized;
        } catch (URISyntaxException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " 형식이 올바르지 않습니다.");
        }
    }
}
