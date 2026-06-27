package com.swatching.swatching_be.domain.brand.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BrandSubmitRequest {

    @NotBlank(message = "브랜드명을 입력해주세요.")
    private String name;

    private String summary;

    private String instagramUrl;

    private String websiteUrl;

    @NotBlank(message = "담당자명을 입력해주세요.")
    private String managerName;

    @NotBlank(message = "담당자 이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식을 입력해주세요.")
    private String managerEmail;

    @NotBlank(message = "담당자 연락처를 입력해주세요.")
    private String managerPhone;
}
