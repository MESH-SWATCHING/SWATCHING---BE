package com.swatching.swatching_be.domain.manualbrand.service;

import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.BrandImage;
import com.swatching.swatching_be.domain.brand.repository.BrandImageRepository;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import com.swatching.swatching_be.domain.category.Category;
import com.swatching.swatching_be.domain.category.repository.CategoryRepository;
import com.swatching.swatching_be.domain.category.service.CategoryService;
import com.swatching.swatching_be.domain.manualbrand.dto.CreateManualBrandRequest;
import com.swatching.swatching_be.domain.manualbrand.dto.ManualBrandResponse;
import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategory;
import com.swatching.swatching_be.domain.savedbrand.repository.SavedBrandCategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.repository.SavedBrandRepository;
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
    private final BrandImageRepository brandImageRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final SavedBrandCategoryRepository savedBrandCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    public ManualBrandService(
            BrandRepository brandRepository,
            BrandImageRepository brandImageRepository,
            SavedBrandRepository savedBrandRepository,
            SavedBrandCategoryRepository savedBrandCategoryRepository,
            CategoryRepository categoryRepository,
            CategoryService categoryService
    ) {
        this.brandRepository = brandRepository;
        this.brandImageRepository = brandImageRepository;
        this.savedBrandRepository = savedBrandRepository;
        this.savedBrandCategoryRepository = savedBrandCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
    }

    @Transactional
    public ManualBrandResponse createManualBrand(User user, CreateManualBrandRequest request) {
        return createManualBrand(user, request, null, List.of());
    }

    @Transactional
    public ManualBrandResponse createManualBrand(
            User user,
            CreateManualBrandRequest request,
            String mainImageUrl,
            List<String> imageUrls
    ) {
        String name = normalizeRequiredText(request.name(), "Brand name is required.", 255);
        String instagramUrl = normalizeUrl(request.instagramUrl(), "instagramUrl");
        String websiteUrl = normalizeUrl(request.websiteUrl(), "websiteUrl");
        String normalizedMainImageUrl = normalizeUrl(mainImageUrl, "mainImageUrl");
        List<String> normalizedImageUrls = normalizeImageUrls(imageUrls);
        String memo = normalizeOptionalText(request.memo(), "Memo must be 500 characters or less.", 500);

        Category defaultCategory = categoryService.getOrCreateDefaultCategory(user);
        List<Category> categories = resolveCategories(user, defaultCategory, request.categoryIds());

        Brand brand = brandRepository.save(Brand.createManual(name, instagramUrl, websiteUrl, normalizedMainImageUrl, user));
        saveBrandImages(brand, normalizedImageUrls);

        SavedBrand savedBrand = savedBrandRepository.save(SavedBrand.create(user, brand, memo));

        List<SavedBrandCategory> links = categories.stream()
                .map(category -> SavedBrandCategory.create(savedBrand, category))
                .toList();
        savedBrandCategoryRepository.saveAll(links);

        List<Long> categoryIds = categories.stream()
                .map(Category::getId)
                .toList();
        return ManualBrandResponse.of(savedBrand.getId(), brand, normalizedImageUrls, categoryIds);
    }

    private void saveBrandImages(Brand brand, List<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            return;
        }

        List<BrandImage> brandImages = imageUrls.stream()
                .map(imageUrl -> BrandImage.create(brand, imageUrl))
                .toList();
        brandImageRepository.saveAll(brandImages);
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
            throw new BusinessException(ErrorCode.NOT_FOUND, "Category not found.");
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
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "Brand name must be 255 characters or less.");
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
        String normalized = normalizeOptionalText(value, fieldName + " must be 2048 characters or less.", 2048);
        if (normalized == null) {
            return null;
        }

        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " must be an http or https URL.");
            }
            if (uri.getHost() == null || uri.getHost().isBlank()) {
                throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " is not a valid URL.");
            }
            return normalized;
        } catch (URISyntaxException exception) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, fieldName + " is not a valid URL.");
        }
    }

    private List<String> normalizeImageUrls(List<String> imageUrls) {
        if (imageUrls == null) {
            return List.of();
        }

        return imageUrls.stream()
                .map(imageUrl -> normalizeUrl(imageUrl, "imageUrl"))
                .filter(imageUrl -> imageUrl != null)
                .distinct()
                .toList();
    }
}
