package com.example.PhuongDungShopWeb.controller;

import com.example.PhuongDungShopWeb.model.Order;
import com.example.PhuongDungShopWeb.model.RewardVoucher;
import com.example.PhuongDungShopWeb.model.User;
import com.example.PhuongDungShopWeb.service.CartService;
import com.example.PhuongDungShopWeb.service.MomoService;
import com.example.PhuongDungShopWeb.service.OrderService;
import com.example.PhuongDungShopWeb.service.RewardService;
import com.example.PhuongDungShopWeb.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final CartService cartService;
    private final OrderService orderService;
    private final MomoService momoService;
    private final UserService userService;
    private final RewardService rewardService;

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("cartPricingMap", cartService.getCartPricingMap());
        model.addAttribute("subtotal", cartService.getSubtotal());
        model.addAttribute("totalQuantity", cartService.getTotalQuantity());
        model.addAttribute("shippingFee", cartService.getShippingFee());
        model.addAttribute("rewardPoints", cartService.getRewardPoints());
        model.addAttribute("grandTotal", cartService.getGrandTotal());
        model.addAttribute("freeShipping", cartService.isFreeShipping());
        RewardVoucher voucher = rewardService.getDefaultVoucher();
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        int userPoints = currentUser == null ? 0 : userService.getRewardPoints(currentUser);
        boolean voucherAvailable = currentUser != null && voucher.getCode().equals(currentUser.getVoucherCode());
        model.addAttribute("voucher", voucher);
        model.addAttribute("voucherAvailable", voucherAvailable);
        model.addAttribute("userPoints", userPoints);
        return "order/checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(@RequestParam String receiverName,
                             @RequestParam String phone,
                             @RequestParam(required = false) String email,
                             @RequestParam String address,
                             @RequestParam(required = false) String note,
                             @RequestParam String paymentMethod,
                             @RequestParam(required = false) String applyVoucher,
                             HttpServletRequest request,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        if (cartService.getCartItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng đang trống.");
            return "redirect:/order/checkout";
        }

        RewardVoucher voucher = rewardService.getDefaultVoucher();
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        String appliedVoucherCode = null;
        double voucherDiscount = 0d;
        if (currentUser != null && applyVoucher != null && voucher.getCode().equals(currentUser.getVoucherCode())) {
            appliedVoucherCode = voucher.getCode();
            voucherDiscount = voucher.getDiscountAmount();
        }

        double totalAmount = Math.max(0, cartService.getGrandTotal() - voucherDiscount);
        Order order = orderService.createOrder(receiverName, phone, email, address, note, paymentMethod,
                cartService.getCartItems(), totalAmount, appliedVoucherCode, voucherDiscount);

        int earnedPoints = cartService.getRewardPoints();
        cartService.clearCart();

        if (currentUser != null) {
            if (earnedPoints > 0) {
                userService.addPoints(currentUser, earnedPoints);
            }
            if (appliedVoucherCode != null) {
                userService.clearVoucher(currentUser);
            }
        }

        if ("MoMo".equals(paymentMethod)) {
            try {
                String host = request.getRequestURL().toString().replace(request.getRequestURI(), "");
                String payUrl = momoService.createPayment(order, host);
                return "redirect:" + payUrl;
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thanh toán MoMo: " + e.getMessage());
                return "redirect:/order/history";
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Mã đơn hàng: #" + order.getId());
        return "redirect:/order/history";
    }

    @GetMapping("/momo-return")
    public String momoReturn(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        String orderIdStr = params.get("orderId");
        String resultCode = params.get("resultCode");
        
        if (orderIdStr != null && resultCode != null) {
            try {
                // Parse orderId từ chuỗi "id_timestamp" do MomoService gửi
                String[] parts = orderIdStr.split("_");
                Long orderId = Long.parseLong(parts[0]);

                if ("0".equals(resultCode)) {
                    orderService.updateOrderStatus(orderId, "PAID");
                    redirectAttributes.addFlashAttribute("successMessage", "Thanh toán MoMo thành công cho đơn hàng #" + orderId);
                    return "redirect:/";
                } else {
                    redirectAttributes.addFlashAttribute("errorMessage", "Thanh toán MoMo thất bại. Vui lòng thử lại hoặc chọn phương thức khác.");
                }
            } catch (NumberFormatException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Lỗi xử lý kết quả thanh toán.");
            }
        }
        return "redirect:/order/history";
    }
    
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        return "order/detail";
    }

    @GetMapping("/history")
    public String orderHistory(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/history";
    }
}
