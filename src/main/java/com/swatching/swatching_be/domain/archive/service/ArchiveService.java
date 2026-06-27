package com.swatching.swatching_be.domain.archive.service;

import com.swatching.swatching_be.domain.archive.converter.ArchiveConverter;
import com.swatching.swatching_be.domain.archive.dto.ArchiveReqDTO;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.brand.Brand;
import com.swatching.swatching_be.domain.brand.repository.BrandRepository;
import com.swatching.swatching_be.domain.category.Category;
import com.swatching.swatching_be.domain.category.CategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategory;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandRepository;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveService {

    private final CategoryRepository categoryRepository;
    private final SavedBrandRepository savedBrandRepository;
    private final SavedBrandCategoryRepository savedBrandCategoryRepository;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;

    public ArchiveResDTO.CategoryListDTO getMySwatchCategories(Long userId) {
        List<Category> categories = categoryRepository.findAllByUserId(userId);

        Long totalSavedBrandCount = savedBrandRepository.countByUserId(userId);

        Map<Long, Long> categoryBrandCountMap = savedBrandCategoryRepository
                .countBrandsByCategoryRaw(userId)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        return ArchiveConverter.toCategoryListDTO(
                totalSavedBrandCount,
                categories,
                categoryBrandCountMap
        );
    }

    @Transactional
    public void saveBrand(Long userId, Long brandId, ArchiveReqDTO.SaveBrandDTO request) {
        if (savedBrandRepository.existsByUserIdAndBrandId(userId, brandId)) {
            throw new IllegalArgumentException("이미 저장된 브랜드입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        SavedBrand savedBrand = SavedBrand.create(user, brand, null);
        savedBrandRepository.save(savedBrand);

        // "전체" 카테고리 자동 포함
        Category defaultCategory = categoryRepository.findByUserIdAndName(userId, Category.DEFAULT_NAME)
                .orElseThrow(() -> new IllegalArgumentException("기본 카테고리를 찾을 수 없습니다."));

        List<SavedBrandCategory> links = new ArrayList<>();
        links.add(SavedBrandCategory.create(savedBrand, defaultCategory));

        // 사용자가 선택한 카테고리 연결
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            List<Category> selectedCategories = categoryRepository
                    .findAllByIdInAndUserId(request.getCategoryIds(), userId);
            for (Category category : selectedCategories) {
                if (!category.getId().equals(defaultCategory.getId())) {
                    links.add(SavedBrandCategory.create(savedBrand, category));
                }
            }
        }

        savedBrandCategoryRepository.saveAll(links);
    }
}
