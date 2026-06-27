package com.swatching.swatching_be.domain.user;

import com.swatching.swatching_be.global.auth.CurrentUserProvider;
import com.swatching.swatching_be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/me")
    public ApiResponse<MeResponse> getMe() {
        User user = currentUserProvider.getCurrentUser();
        return ApiResponse.success("내 정보 조회 성공", MeResponse.from(user));
    }
}
