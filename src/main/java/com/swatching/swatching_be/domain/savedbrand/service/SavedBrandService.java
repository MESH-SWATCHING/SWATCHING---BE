package com.swatching.swatching_be.domain.savedbrand.service;

import com.swatching.swatching_be.domain.savedbrand.SavedBrand;
import com.swatching.swatching_be.domain.savedbrand.dto.SavedBrandMemoResponse;
import com.swatching.swatching_be.domain.savedbrand.dto.UpdateSavedBrandMemoRequest;
import com.swatching.swatching_be.domain.savedbrand.repository.SavedBrandRepository;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SavedBrandService {

    private final SavedBrandRepository savedBrandRepository;

    public SavedBrandService(SavedBrandRepository savedBrandRepository) {
        this.savedBrandRepository = savedBrandRepository;
    }

    @Transactional
    public SavedBrandMemoResponse updateMemo(User user, Long savedBrandId, UpdateSavedBrandMemoRequest request) {
        SavedBrand savedBrand = savedBrandRepository.findByIdAndUserId(savedBrandId, user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "저장 브랜드를 찾을 수 없습니다."));

        String memo = normalizeMemo(request.memo());
        savedBrand.updateMemo(memo);
        return SavedBrandMemoResponse.from(savedBrand);
    }

    private String normalizeMemo(String memo) {
        String normalized = memo == null ? "" : memo.trim();
        if (normalized.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "메모는 필수입니다.");
        }
        if (normalized.length() > 500) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "메모는 최대 500자까지 가능합니다.");
        }
        return normalized;
    }
}
