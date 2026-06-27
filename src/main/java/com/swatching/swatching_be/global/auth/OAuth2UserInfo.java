package com.swatching.swatching_be.global.auth;

public interface OAuth2UserInfo {
    String getProviderId();
    String getEmail();
    String getNickname();
    OAuthProvider getProvider();
}
