package com.example.PhuongDungShopWeb.service;

import com.example.PhuongDungShopWeb.model.Category;
import com.example.PhuongDungShopWeb.model.Product;
import com.example.PhuongDungShopWeb.model.PromotionType;
import com.example.PhuongDungShopWeb.repository.CategoryRepository;
import com.example.PhuongDungShopWeb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::syncPromotionStateIfNeeded)
                .toList();
    }

    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow();
        return syncPromotionStateIfNeeded(product);
    }

    public Product saveProduct(Product product) {
        product.setCategory(resolveCategory(product.getCategory()));
        normalizePromotionFields(product);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updated) {
        Product existing = productRepository.findById(id).orElseThrow();
        existing.setName(updated.getName());
        existing.setPrice(updated.getPrice());
        existing.setDescription(updated.getDescription());
        existing.setImageUrl(updated.getImageUrl());
        existing.setCategory(resolveCategory(updated.getCategory()));
        existing.setPromotionType(updated.getPromotionType());
        existing.setDiscountPercent(updated.getDiscountPercent());
        existing.setGiftDescription(updated.getGiftDescription());
        existing.setPromotionQuantity(updated.getPromotionQuantity());
        normalizePromotionFields(existing);
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::syncPromotionStateIfNeeded)
                .toList();
    }

    public List<Product> getProductsByPromotionType(PromotionType promotionType) {
        return productRepository.findByPromotionType(promotionType).stream()
                .map(this::syncPromotionStateIfNeeded)
                .toList();
    }

    public Product updatePromotionAfterPurchase(Product product, int usedPromotionQuantity) {
        int currentPromotionQuantity = Math.max(0, product.getPromotionQuantity() == null ? 0 : product.getPromotionQuantity());
        int consumed = Math.max(0, usedPromotionQuantity);
        int remaining = Math.max(0, currentPromotionQuantity - consumed);
        product.setPromotionQuantity(remaining);
        if (remaining > 0) {
            return productRepository.save(product);
        }
        if (product.getPromotionType() == PromotionType.DISCOUNT) {
            if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
                product.setPrice(product.getOriginalPrice());
            }
            product.setOriginalPrice(null);
            product.setDiscountPercent(null);
        }
        if (product.getPromotionType() == PromotionType.GIFT) {
            product.setGiftDescription(null);
        }
        product.setPromotionType(PromotionType.NONE);
        return productRepository.save(product);
    }

    private Category resolveCategory(Category category) {
        if (category == null || category.getId() == null) {
            return null;
        }
        return categoryRepository.findById(category.getId()).orElse(null);
    }

    private Product syncPromotionStateIfNeeded(Product product) {
        if (product.getPromotionType() == PromotionType.DISCOUNT
                && (product.getPromotionQuantity() == null || product.getPromotionQuantity() <= 0)) {
            if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
                product.setPrice(product.getOriginalPrice());
            }
            product.setOriginalPrice(null);
            product.setDiscountPercent(null);
            product.setPromotionType(PromotionType.NONE);
            product.setPromotionQuantity(0);
            return productRepository.save(product);
        }
        if (product.getPromotionQuantity() == null || product.getPromotionQuantity() < 0) {
            product.setPromotionQuantity(0);
            return productRepository.save(product);
        }
        return product;
    }

    private void normalizePromotionFields(Product product) {
        PromotionType promotionType = product.getPromotionType() == null ? PromotionType.NONE : product.getPromotionType();
        product.setPromotionType(promotionType);
        int promotionQuantity = Math.max(0, product.getPromotionQuantity() == null ? 0 : product.getPromotionQuantity());
        product.setPromotionQuantity(promotionQuantity);

        if (promotionType == PromotionType.DISCOUNT) {
            double discountPercent = Math.max(0d, product.getDiscountPercent() == null ? 0d : product.getDiscountPercent());
            discountPercent = Math.min(discountPercent, 100d);
            product.setDiscountPercent(discountPercent);
            product.setGiftDescription(null);

            if (discountPercent == 0d || promotionQuantity == 0) {
                if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
                    product.setPrice(product.getOriginalPrice());
                }
                product.setOriginalPrice(null);
                product.setDiscountPercent(null);
                product.setPromotionType(PromotionType.NONE);
                product.setPromotionQuantity(0);
                return;
            }

            double originalPrice = product.getOriginalPrice() != null && product.getOriginalPrice() > 0
                    ? product.getOriginalPrice()
                    : product.getPrice();
            product.setOriginalPrice(originalPrice);
            product.setPrice(roundCurrency(originalPrice * (100d - discountPercent) / 100d));
            return;
        }

        if (promotionType == PromotionType.GIFT) {
            product.setDiscountPercent(null);
            product.setOriginalPrice(null);
            if (promotionQuantity == 0) {
                product.setPromotionType(PromotionType.NONE);
                product.setGiftDescription(null);
            }
            return;
        }

        if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
            product.setPrice(product.getOriginalPrice());
        }
        product.setOriginalPrice(null);
        product.setDiscountPercent(null);
        product.setGiftDescription(null);
        product.setPromotionQuantity(0);
    }

    private double roundCurrency(double amount) {
        return Math.round(amount);
    }
}
