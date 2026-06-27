package com.swatching.swatching_be.domain.user;

public record MeResponse(Long userId, String nickname, String email, String role) {
    public static MeResponse from(User user) {
        return new MeResponse(user.getId(), user.getNickname(), user.getEmail(), user.getRole().name());
    }
}
