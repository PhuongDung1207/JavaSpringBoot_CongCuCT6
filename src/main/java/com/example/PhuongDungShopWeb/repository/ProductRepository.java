package com.example.PhuongDungShopWeb.repository;

import com.example.PhuongDungShopWeb.model.Product;
import com.example.PhuongDungShopWeb.model.PromotionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    java.util.List<Product> findByCategoryId(Long categoryId);
    java.util.List<Product> findByPromotionType(PromotionType promotionType);
}
