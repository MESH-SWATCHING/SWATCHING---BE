package com.swatching.swatching_be.global.auth;

import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.domain.user.UserRepository;
import com.swatching.swatching_be.global.exception.BusinessException;
import com.swatching.swatching_be.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CurrentUserProvider {

    public static final String CURRENT_USER_HEADER = "X-USER-ID";

    private final HttpServletRequest request;
    private final UserRepository userRepository;

    public CurrentUserProvider(HttpServletRequest request, UserRepository userRepository) {
        this.request = request;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User getCurrentUser() {
        Long userId = parseUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.UNAUTHORIZED, "존재하지 않는 사용자입니다."));
    }

    private Long parseUserId() {
        String header = request.getHeader(CURRENT_USER_HEADER);
        if (header == null || header.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "X-USER-ID 헤더가 필요합니다.");
        }
        try {
            return Long.parseLong(header);
        } catch (NumberFormatException exception) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "X-USER-ID 헤더는 숫자여야 합니다.");
        }
    }
}
