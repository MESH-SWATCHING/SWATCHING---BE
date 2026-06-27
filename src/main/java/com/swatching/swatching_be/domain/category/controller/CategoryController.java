package com.swatching.swatching_be.domain.category.controller;

import com.swatching.swatching_be.domain.category.dto.CategoryResponse;
import com.swatching.swatching_be.domain.category.dto.CreateCategoryRequest;
import com.swatching.swatching_be.domain.category.service.CategoryService;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CurrentUserProvider currentUserProvider;
    private final CategoryService categoryService;

    public CategoryController(CurrentUserProvider currentUserProvider, CategoryService categoryService) {
        this.currentUserProvider = currentUserProvider;
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        User currentUser = currentUserProvider.getCurrentUser();
        return ApiResponse.success("카테고리 생성 성공", categoryService.createCategory(currentUser, request));
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long categoryId) {
        User currentUser = currentUserProvider.getCurrentUser();
        categoryService.deleteCategory(currentUser, categoryId);
    }
}
