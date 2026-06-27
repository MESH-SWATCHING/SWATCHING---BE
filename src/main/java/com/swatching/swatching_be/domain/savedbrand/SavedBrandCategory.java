package com.swatching.swatching_be.domain.savedbrand;

import com.swatching.swatching_be.domain.category.Category;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "saved_brand_categories",
        uniqueConstraints = @UniqueConstraint(columnNames = {"saved_brand_id", "category_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SavedBrandCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saved_brand_id", nullable = false)
    private SavedBrand savedBrand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public static SavedBrandCategory create(SavedBrand savedBrand, Category category) {
        SavedBrandCategory link = new SavedBrandCategory();
        link.savedBrand = savedBrand;
        link.category = category;
        return link;
    }
}
