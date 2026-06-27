package com.swatching.swatching_be.brand.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandId;

    @Column(nullable = false)
    private String name;

    private String oneLineIntro;

    @Column(columnDefinition = "TEXT")
    private String story;

    private String thumbnailUrl;

    private String instagramUrl;

    private String websiteUrl;
}