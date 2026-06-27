package com.swatching.swatching_be.domain.archive.entity.mapping;

import com.swatching.swatching_be.domain.archive.entity.SavedBrand;
import com.swatching.swatching_be.domain.archive.entity.UserCategory;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedBrandCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_brand_id")
    private SavedBrand savedBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_category_id")
    private UserCategory userCategory;
}
