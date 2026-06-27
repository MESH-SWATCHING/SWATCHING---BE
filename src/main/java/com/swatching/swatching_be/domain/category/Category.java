package com.swatching.swatching_be.domain.category;

import com.swatching.swatching_be.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "name"})
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    public static final String DEFAULT_NAME = "전체";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public static Category create(User user, String name, boolean isDefault) {
        Category category = new Category();
        category.user = user;
        category.name = name;
        category.isDefault = isDefault;
        return category;
    }

    public static boolean isDefaultName(String name) {
        return DEFAULT_NAME.equals(name);
    }
}
