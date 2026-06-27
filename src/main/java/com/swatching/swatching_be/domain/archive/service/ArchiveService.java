package com.swatching.swatching_be.domain.archive.service;

import com.swatching.swatching_be.domain.archive.converter.ArchiveConverter;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.category.Category;
import com.swatching.swatching_be.domain.category.CategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandCategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.SavedBrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
