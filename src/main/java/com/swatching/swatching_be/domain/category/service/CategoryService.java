package com.swatching.swatching_be.domain.category.service;

import com.swatching.swatching_be.domain.category.Category;
import com.swatching.swatching_be.domain.category.dto.CategoryResponse;
import com.swatching.swatching_be.domain.category.dto.CreateCategoryRequest;
import com.swatching.swatching_be.domain.category.repository.CategoryRepository;
import com.swatching.swatching_be.domain.savedbrand.repository.SavedBrandCategoryRepository;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SavedBrandCategoryRepository savedBrandCategoryRepository;

    public CategoryService(
            CategoryRepository categoryRepository,
            SavedBrandCategoryRepository savedBrandCategoryRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.savedBrandCategoryRepository = savedBrandCategoryRepository;
    }

    @Transactional
    public CategoryResponse createCategory(User user, CreateCategoryRequest request) {
        String name = normalizeName(request.name());
        validateCreatableName(name);

        if (categoryRepository.existsByUserIdAndName(user.getId(), name)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY);
        }

        try {
            Category category = categoryRepository.save(Category.create(user, name, false));
            return CategoryResponse.from(category);
        } catch (DataIntegrityViolationException exception) {
            throw new BusinessException(ErrorCode.DUPLICATE_CATEGORY);
        }
    }

    @Transactional
    public void deleteCategory(User user, Long categoryId) {
        Category category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "카테고리를 찾을 수 없습니다."));

        if (category.isDefault()) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "기본 카테고리는 삭제할 수 없습니다.");
        }

        savedBrandCategoryRepository.deleteAllByCategoryId(category.getId());
        categoryRepository.delete(category);
    }

    @Transactional
    public Category getOrCreateDefaultCategory(User user) {
        return categoryRepository.findByUserIdAndName(user.getId(), Category.DEFAULT_NAME)
                .orElseGet(() -> categoryRepository.save(Category.create(user, Category.DEFAULT_NAME, true)));
    }

    private String normalizeName(String name) {
        String normalized = name == null ? "" : name.trim();
        if (normalized.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "카테고리 이름은 필수입니다.");
        }
        if (normalized.length() > 50) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "카테고리 이름은 최대 50자까지 가능합니다.");
        }
        return normalized;
    }

    private void validateCreatableName(String name) {
        if (Category.isDefaultName(name)) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "기본 카테고리는 생성 API로 만들 수 없습니다.");
        }
    }
}
