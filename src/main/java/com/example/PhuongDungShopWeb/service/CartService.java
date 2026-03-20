package com.example.PhuongDungShopWeb.service;

import com.example.PhuongDungShopWeb.model.CartItem;
import com.example.PhuongDungShopWeb.model.Product;
import com.example.PhuongDungShopWeb.model.PromotionType;
import com.example.PhuongDungShopWeb.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Service; 
import org.springframework.web.context.annotation.SessionScope; 
 
import java.util.ArrayList; 
import java.util.LinkedHashMap;
import java.util.List; 
import java.util.Map;
import java.util.Optional;
 
@Service 
@SessionScope 
public class CartService { 
    private static final double FREE_SHIP_THRESHOLD = 1_000_000d;
    private static final int FREE_SHIP_MIN_QUANTITY = 2;
    private static final double SHIPPING_FEE = 30_000d;
    private static final double REWARD_POINT_AMOUNT = 15_000d;

    private List<CartItem> cartItems = new ArrayList<>(); 

    @Autowired 
    private ProductRepository productRepository; 

    public void addToCart(Long productId, int quantity) { 
        Product product = productRepository.findById(productId).orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId)); 
        int validQuantity = Math.max(quantity, 1);
        
        // Kiểm tra số lượng khuyến mãi
        if (product.getPromotionType() == PromotionType.DISCOUNT) {
             int maxPromo = product.getPromotionQuantity() != null ? product.getPromotionQuantity() : 0;
             int currentInCart = 0;
             
             Optional<CartItem> existingItemCheck = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
             
             if (existingItemCheck.isPresent()) {
                 currentInCart = existingItemCheck.get().getQuantity();
             }
             
             if (currentInCart + validQuantity > maxPromo) {
                 validQuantity = maxPromo - currentInCart;
                 if (validQuantity <= 0) return; // Không thể thêm quá số lượng khuyến mãi
             }
        }

        Optional<CartItem> existingItem = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + validQuantity);
            // Update product info in cart to latest
            item.setProduct(product);
            return;
        }
        cartItems.add(new CartItem(product, validQuantity)); 
    } 
 
    public List<CartItem> getCartItems() { 
        return cartItems; 
    } 

    public double getSubtotal() {
        return getCartPricingMap().values().stream()
                .mapToDouble(CartLinePricing::getLineTotal)
                .sum();
    }

    public Map<Long, CartLinePricing> getCartPricingMap() {
        Map<Long, CartLinePricing> pricingMap = new LinkedHashMap<>();
        for (CartItem item : cartItems) {
            pricingMap.put(item.getProduct().getId(), calculateLinePricing(item));
        }
        return pricingMap;
    }

    public int getTotalQuantity() {
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isFreeShipping() {
        return getSubtotal() >= FREE_SHIP_THRESHOLD && getTotalQuantity() >= FREE_SHIP_MIN_QUANTITY;
    }

    public double getShippingFee() {
        return isFreeShipping() ? 0 : SHIPPING_FEE;
    }

    public int getRewardPoints() {
        return (int) (Math.floor(getSubtotal() / REWARD_POINT_AMOUNT) * 2);
    }

    public double getGrandTotal() {
        return getSubtotal() + getShippingFee();
    }
 
    public void removeFromCart(Long productId) { 
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId)); 
    } 
 
    public void clearCart() { 
        cartItems.clear(); 
    } 

    private CartLinePricing calculateLinePricing(CartItem item) {
        Product latestProduct = productRepository.findById(item.getProduct().getId()).orElse(item.getProduct());
        int quantity = Math.max(item.getQuantity(), 0);
        double originalUnitPrice = resolveOriginalUnitPrice(latestProduct);
        boolean isDiscountPromotion = latestProduct.getPromotionType() == PromotionType.DISCOUNT
                && latestProduct.getDiscountPercent() != null
                && latestProduct.getDiscountPercent() > 0;
        int promotionQuantity = Math.max(0, latestProduct.getPromotionQuantity() == null ? 0 : latestProduct.getPromotionQuantity());
        int discountedQuantity = isDiscountPromotion ? Math.min(quantity, promotionQuantity) : 0;
        int regularQuantity = quantity - discountedQuantity;
        double discountedUnitPrice = isDiscountPromotion
                ? roundCurrency(originalUnitPrice * (100d - latestProduct.getDiscountPercent()) / 100d)
                : originalUnitPrice;
        double lineTotal = roundCurrency(discountedQuantity * discountedUnitPrice + regularQuantity * originalUnitPrice);
        double discountAmount = roundCurrency((originalUnitPrice - discountedUnitPrice) * discountedQuantity);
        return new CartLinePricing(discountedQuantity, regularQuantity, originalUnitPrice, discountedUnitPrice, lineTotal, discountAmount);
    }

    private double resolveOriginalUnitPrice(Product product) {
        if (product.getOriginalPrice() != null && product.getOriginalPrice() > 0) {
            return product.getOriginalPrice();
        }
        return product.getPrice();
    }

    private double roundCurrency(double amount) {
        return Math.round(amount);
    }

    public static class CartLinePricing {
        private final int discountedQuantity;
        private final int regularQuantity;
        private final double originalUnitPrice;
        private final double discountedUnitPrice;
        private final double lineTotal;
        private final double discountAmount;

        public CartLinePricing(int discountedQuantity, int regularQuantity, double originalUnitPrice,
                               double discountedUnitPrice, double lineTotal, double discountAmount) {
            this.discountedQuantity = discountedQuantity;
            this.regularQuantity = regularQuantity;
            this.originalUnitPrice = originalUnitPrice;
            this.discountedUnitPrice = discountedUnitPrice;
            this.lineTotal = lineTotal;
            this.discountAmount = discountAmount;
        }

        public int getDiscountedQuantity() {
            return discountedQuantity;
        }

        public int getRegularQuantity() {
            return regularQuantity;
        }

        public double getOriginalUnitPrice() {
            return originalUnitPrice;
        }

        public double getDiscountedUnitPrice() {
            return discountedUnitPrice;
        }

        public double getLineTotal() {
            return lineTotal;
        }

        public double getDiscountAmount() {
            return discountAmount;
        }
    }
} 
