package com.example.PhuongDungShopWeb.model;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private String description;
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private PromotionType promotionType = PromotionType.NONE;

    private Double discountPercent;

    private String giftDescription;

    private Integer promotionQuantity = 0;

    private Double originalPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public PromotionType getPromotionType() { return promotionType; }
    public void setPromotionType(PromotionType promotionType) { this.promotionType = promotionType; }

    public Double getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(Double discountPercent) { this.discountPercent = discountPercent; }

    public String getGiftDescription() { return giftDescription; }
    public void setGiftDescription(String giftDescription) { this.giftDescription = giftDescription; }

    public Integer getPromotionQuantity() { return promotionQuantity; }
    public void setPromotionQuantity(Integer promotionQuantity) { this.promotionQuantity = promotionQuantity; }

    public Double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(Double originalPrice) { this.originalPrice = originalPrice; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
