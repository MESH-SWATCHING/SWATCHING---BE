package com.swatching.swatching_be.brand.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BrandImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long brandImageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    private String imageUrl;

    private Integer sortOrder;
}