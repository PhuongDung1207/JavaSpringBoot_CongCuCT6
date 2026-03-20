package com.example.PhuongDungShopWeb.controller;

import com.example.PhuongDungShopWeb.model.RewardVoucher;
import com.example.PhuongDungShopWeb.model.User;
import com.example.PhuongDungShopWeb.service.RewardService;
import com.example.PhuongDungShopWeb.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/rewards")
@RequiredArgsConstructor
public class RewardController {
    private final UserService userService;
    private final RewardService rewardService;

    @GetMapping("/lookup")
    public String lookupForm(@RequestParam(required = false) String phone, Model model, Principal principal) {
        RewardVoucher voucher = rewardService.getDefaultVoucher();
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("voucher", voucher);
        model.addAttribute("phone", phone == null ? "" : phone);
        if (currentUser != null) {
            model.addAttribute("userPoints", userService.getRewardPoints(currentUser));
            model.addAttribute("voucherAvailable", voucher.getCode().equals(currentUser.getVoucherCode()));
        }
        if (currentUser != null && phone != null && !phone.isBlank()) {
            if (phone.equals(currentUser.getPhone())) {
                model.addAttribute("lookupDone", true);
                model.addAttribute("canRedeem", userService.canRedeemVoucher(currentUser, voucher));
            } else {
                model.addAttribute("errorMessage", "So dien thoai khong khop voi tai khoan dang nhap.");
            }
        }
        return "rewards/lookup";
    }

    @PostMapping("/lookup")
    public String lookup(@RequestParam String phone, Principal principal, RedirectAttributes redirectAttributes) {
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || phone == null || phone.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui long nhap so dien thoai.");
            return "redirect:/rewards/lookup";
        }
        if (!phone.equals(currentUser.getPhone())) {
            redirectAttributes.addFlashAttribute("errorMessage", "So dien thoai khong khop voi tai khoan dang nhap.");
            return "redirect:/rewards/lookup";
        }
        return "redirect:/rewards/lookup?phone=" + phone;
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String phone, Principal principal, HttpSession session,
                          RedirectAttributes redirectAttributes) {
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || phone == null || phone.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong tim thay thong tin nguoi dung.");
            return "redirect:/rewards/lookup";
        }
        if (!phone.equals(currentUser.getPhone())) {
            redirectAttributes.addFlashAttribute("errorMessage", "So dien thoai khong khop voi tai khoan dang nhap.");
            return "redirect:/rewards/lookup";
        }
        String otp = rewardService.generateOtp(session, phone);
        String email = currentUser.getEmail();
        try {
            rewardService.sendOtpEmail(email, otp);
            redirectAttributes.addFlashAttribute("successMessage", "Da gui OTP toi email: " + maskEmail(email));
        } catch (Exception ex) {
            if (!rewardService.isMailConfigured()) {
                redirectAttributes.addFlashAttribute("infoMessage",
                        "Email chua duoc cau hinh. OTP (demo) la: " + otp);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Gui OTP that bai: " + ex.getMessage());
            }
        }
        return "redirect:/rewards/lookup?phone=" + phone;
    }

    private String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return "";
        }
        int at = email.indexOf('@');
        if (at <= 1) {
            return "***" + email.substring(at);
        }
        return email.substring(0, 2) + "***" + email.substring(at);
    }

    @PostMapping("/redeem")
    public String redeem(@RequestParam String phone, @RequestParam String otp, Principal principal,
                         HttpSession session, RedirectAttributes redirectAttributes) {
        RewardVoucher voucher = rewardService.getDefaultVoucher();
        User currentUser = principal == null ? null : userService.findByUsername(principal.getName()).orElse(null);
        if (currentUser == null || phone == null || phone.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong tim thay thong tin nguoi dung.");
            return "redirect:/rewards/lookup";
        }
        if (!phone.equals(currentUser.getPhone())) {
            redirectAttributes.addFlashAttribute("errorMessage", "So dien thoai khong khop voi tai khoan dang nhap.");
            return "redirect:/rewards/lookup";
        }
        if (!rewardService.validateOtp(session, phone, otp)) {
            redirectAttributes.addFlashAttribute("errorMessage", "OTP khong hop le hoac da het han.");
            return "redirect:/rewards/lookup?phone=" + phone;
        }
        if (!userService.canRedeemVoucher(currentUser, voucher)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong du diem hoac ban da co voucher.");
            return "redirect:/rewards/lookup?phone=" + phone;
        }
        userService.redeemVoucher(currentUser, voucher);
        rewardService.clearOtp(session);
        redirectAttributes.addFlashAttribute("successMessage", "Doi diem thanh cong. Voucher cua ban: " + voucher.getCode());
        return "redirect:/rewards/lookup?phone=" + phone;
    }
}
