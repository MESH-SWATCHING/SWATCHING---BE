package com.swatching.swatching_be.domain.brand;

import com.swatching.swatching_be.domain.keyword.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "brand_keywords")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    public static BrandKeyword create(Brand brand, Keyword keyword) {
        BrandKeyword brandKeyword = new BrandKeyword();
        brandKeyword.brand = brand;
        brandKeyword.keyword = keyword;
        return brandKeyword;
    }
}
