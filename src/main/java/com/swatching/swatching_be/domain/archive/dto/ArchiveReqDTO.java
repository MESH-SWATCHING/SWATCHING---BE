package com.swatching.swatching_be.domain.archive.dto;

import lombok.Getter;

import java.util.List;

public class ArchiveReqDTO {

    @Getter
    public static class SaveBrandDTO {
        private List<Long> categoryIds;
    }
}
