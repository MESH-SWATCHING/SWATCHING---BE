package com.swatching.swatching_be.global.auth;

import com.swatching.swatching_be.domain.category.service.CategoryService;
import com.swatching.swatching_be.domain.user.User;
import com.swatching.swatching_be.domain.user.UserRepository;
import com.swatching.swatching_be.domain.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final CategoryService categoryService;
    private final List<String> adminEmails;

    public CustomOAuth2UserService(
            UserRepository userRepository,
            CategoryService categoryService,
            @Value("${app.admin.emails:}") List<String> adminEmails
    ) {
        this.userRepository = userRepository;
        this.categoryService = categoryService;
        this.adminEmails = adminEmails;
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = resolveUserInfo(registrationId, oAuth2User.getAttributes());

        User user = userRepository.findByProviderAndProviderId(userInfo.getProvider(), userInfo.getProviderId())
                .orElseGet(() -> createUser(userInfo));

        return new DefaultOAuth2User(
                user.getAuthorities(),
                Map.of("userId", user.getId()),
                "userId"
        );
    }

    private User createUser(OAuth2UserInfo userInfo) {
        UserRole role = adminEmails.contains(userInfo.getEmail()) ? UserRole.ADMIN : UserRole.USER;
        User user = userRepository.save(
                User.create(userInfo.getNickname(), userInfo.getEmail(), userInfo.getProvider(), userInfo.getProviderId(), role)
        );
        categoryService.getOrCreateDefaultCategory(user);
        return user;
    }

    private OAuth2UserInfo resolveUserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return new GoogleOAuth2UserInfo(attributes);
        }
        throw new OAuth2AuthenticationException("지원하지 않는 OAuth2 제공자입니다: " + registrationId);
    }
}
