package com.swatching.swatching_be.domain.archive.dto;

import lombok.Getter;

import java.util.List;

public class ArchiveReqDTO {

    @Getter
    public static class SaveBrandDTO {
        private List<Long> categoryIds;
    }

    @Getter
    public static class AddSavedBrandsToCategoryDTO {
        private List<Long> savedBrandIds;
    }
}
