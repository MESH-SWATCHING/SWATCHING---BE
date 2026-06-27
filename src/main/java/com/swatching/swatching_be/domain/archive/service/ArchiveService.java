package com.swatching.swatching_be.domain.archive.service;

import com.swatching.swatching_be.domain.archive.converter.ArchiveConverter;
import com.swatching.swatching_be.domain.archive.dto.ArchiveResDTO;
import com.swatching.swatching_be.domain.archive.entity.UserCategory;
import com.swatching.swatching_be.domain.archive.repository.SavedBrandRepository;
import com.swatching.swatching_be.domain.archive.repository.UserCategoryRepository;
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

    private final UserCategoryRepository userCategoryRepository;
    private final SavedBrandRepository savedBrandRepository;

    public ArchiveResDTO.CategoryListDTO getMySwatchCategories(Long userId) {
        List<UserCategory> categories = userCategoryRepository.findAllByUserId(userId);

        Long totalSavedBrandCount = savedBrandRepository.countByUserId(userId);

        Map<Long, Long> categoryBrandCountMap = userCategoryRepository
                .countBrandsByCategory(userId)
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
