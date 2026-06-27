package com.swatching.swatching_be.domain.brand.entity;

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
}
