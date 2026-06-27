package com.swatching.swatching_be.domain.brand;

import com.swatching.swatching_be.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brands")
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

    private String instagramUrl;

    private String websiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandSourceType sourceType = BrandSourceType.OFFICIAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BrandVisibility visibility = BrandVisibility.PUBLIC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    public static Brand createManual(String name, String instagramUrl, String websiteUrl, User ownerUser) {
        Brand brand = new Brand();
        brand.name = name;
        brand.instagramUrl = instagramUrl;
        brand.websiteUrl = websiteUrl;
        brand.sourceType = BrandSourceType.MANUAL;
        brand.visibility = BrandVisibility.PRIVATE;
        brand.ownerUser = ownerUser;
        return brand;
    }
}
