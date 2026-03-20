package com.example.PhuongDungShopWeb.service;

import com.example.PhuongDungShopWeb.model.CartItem;
import com.example.PhuongDungShopWeb.model.Order;
import com.example.PhuongDungShopWeb.model.OrderDetail;
import com.example.PhuongDungShopWeb.model.Product;
import com.example.PhuongDungShopWeb.model.PromotionType;
import com.example.PhuongDungShopWeb.repository.OrderDetailRepository;
import com.example.PhuongDungShopWeb.repository.OrderRepository;
import com.example.PhuongDungShopWeb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public Order createOrder(String customerName, String phone, String email, String address, String note,
                             String paymentMethod, List<CartItem> cartItems, double totalAmount,
                             String voucherCode, double voucherDiscount) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setEmail(email);
        order.setAddress(address);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setVoucherCode(voucherCode);
        order.setVoucherDiscount(voucherDiscount);
        order.setOrderDate(LocalDateTime.now());
        
        // Trạng thái đơn hàng
        if ("MoMo".equalsIgnoreCase(paymentMethod)) {
            order.setStatus("PENDING_PAYMENT");
        } else {
            order.setStatus("PENDING");
        }

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {
            Product product = productRepository.findById(cartItem.getProduct().getId()).orElseThrow();
            
            // Xử lý giảm số lượng khuyến mãi
            if (product.getPromotionType() == PromotionType.DISCOUNT) {
                int buyQuantity = cartItem.getQuantity();
                int currentPromoQuantity = product.getPromotionQuantity() != null ? product.getPromotionQuantity() : 0;
                
                // Trừ số lượng khuyến mãi
                int newPromoQuantity = Math.max(0, currentPromoQuantity - buyQuantity);
                product.setPromotionQuantity(newPromoQuantity);
                
                // Nếu hết khuyến mãi -> Reset về giá gốc
                if (newPromoQuantity == 0) {
                    product.setPromotionType(PromotionType.NONE);
                    if (product.getOriginalPrice() != null) {
                         product.setPrice(product.getOriginalPrice());
                    }
                    product.setDiscountPercent(0.0);
                }
                productRepository.save(product);
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(cartItem.getQuantity());
            detail.setPrice(product.getPrice()); // Lưu giá tại thời điểm mua
            
            orderDetailRepository.save(detail);
        }
        
        return savedOrder;
    }

    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng: " + id));
    }
}
