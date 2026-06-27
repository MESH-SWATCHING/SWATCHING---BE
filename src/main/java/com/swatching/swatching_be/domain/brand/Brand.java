package com.swatching.swatching_be.domain.brand;

import com.swatching.swatching_be.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "brands")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String summary;

    @Column(columnDefinition = "TEXT")
    private String story;

    private String storySummary;

    private String mainImageUrl;

    @Column(length = 2048)
    private String instagramUrl;

    @Column(length = 2048)
    private String websiteUrl;

    private String managerName;

    private String managerEmail;

    private String managerPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandSourceType sourceType = BrandSourceType.OFFICIAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandVisibility visibility = BrandVisibility.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandApprovalStatus approvalStatus = BrandApprovalStatus.APPROVED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Brand createManual(String name, String instagramUrl, String websiteUrl, String mainImageUrl, User ownerUser) {
        Brand brand = new Brand();
        brand.name = name;
        brand.instagramUrl = instagramUrl;
        brand.websiteUrl = websiteUrl;
        brand.mainImageUrl = mainImageUrl;
        brand.sourceType = BrandSourceType.MANUAL;
        brand.visibility = BrandVisibility.PRIVATE;
        brand.ownerUser = ownerUser;
        return brand;
    }
}
