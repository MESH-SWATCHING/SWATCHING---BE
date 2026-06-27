package com.swatching.swatching_be.domain.savedbrand;

import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/saved-brands")
public class SavedBrandController {

    private final CurrentUserProvider currentUserProvider;
    private final SavedBrandService savedBrandService;

    public SavedBrandController(CurrentUserProvider currentUserProvider, SavedBrandService savedBrandService) {
        this.currentUserProvider = currentUserProvider;
        this.savedBrandService = savedBrandService;
    }

    @PatchMapping("/{savedBrandId}/memo")
    public ApiResponse<SavedBrandMemoResponse> updateMemo(
            @PathVariable Long savedBrandId,
            @Valid @RequestBody UpdateSavedBrandMemoRequest request
    ) {
        User currentUser = currentUserProvider.getCurrentUser();
        return ApiResponse.success("저장 브랜드 메모 수정 성공", savedBrandService.updateMemo(currentUser, savedBrandId, request));
    }
}
