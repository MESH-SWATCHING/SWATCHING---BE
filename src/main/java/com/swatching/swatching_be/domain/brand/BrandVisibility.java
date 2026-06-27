package com.swatching.swatching_be.domain.brand;

public enum BrandVisibility {
    PENDING,    // 승인 대기
    PUBLIC,     // 승인 완료 (노출)
    REJECTED,   // 반려
    PRIVATE     // 직접 추가 브랜드 (비공개)
}
