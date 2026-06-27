package com.swatching.swatching_be.domain.user;

import com.swatching.swatching_be.global.auth.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
