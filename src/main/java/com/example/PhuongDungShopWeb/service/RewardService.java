package com.example.PhuongDungShopWeb.service;

import com.example.PhuongDungShopWeb.model.RewardVoucher;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class RewardService {
    private static final RewardVoucher DEFAULT_VOUCHER =
            new RewardVoucher("TGDDSALE50", "Voucher giam 50,000d", 100, 50_000d);
    private static final String OTP_CODE_KEY = "rewardOtpCode";
    private static final String OTP_PHONE_KEY = "rewardOtpPhone";
    private static final String OTP_EXPIRES_KEY = "rewardOtpExpiresAt";
    private static final long OTP_TTL_MS = 5 * 60 * 1000L;
    private final SecureRandom random = new SecureRandom();
    private final JavaMailSender mailSender;
    @Value("${spring.mail.host:}")
    private String mailHost;
    @Value("${spring.mail.username:}")
    private String mailUsername;
    @Value("${app.mail.from:}")
    private String mailFrom;

    public RewardService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public RewardVoucher getDefaultVoucher() {
        return DEFAULT_VOUCHER;
    }

    public String generateOtp(HttpSession session, String phone) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        session.setAttribute(OTP_CODE_KEY, otp);
        session.setAttribute(OTP_PHONE_KEY, phone);
        session.setAttribute(OTP_EXPIRES_KEY, System.currentTimeMillis() + OTP_TTL_MS);
        return otp;
    }

    public boolean isMailConfigured() {
        return mailHost != null && !mailHost.isBlank()
                && mailUsername != null && !mailUsername.isBlank();
    }

    public void sendOtpEmail(String toEmail, String otp) {
        if (toEmail == null || toEmail.isBlank()) {
            throw new IllegalArgumentException("Email khong hop le.");
        }
        if (!isMailConfigured()) {
            throw new IllegalStateException("Email chua duoc cau hinh.");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Ma OTP doi diem tich luy");
        message.setText("Ma OTP cua ban la: " + otp + "\nMa co hieu luc trong 5 phut.");
        String from = (mailFrom != null && !mailFrom.isBlank()) ? mailFrom : mailUsername;
        if (from != null && !from.isBlank()) {
            message.setFrom(from);
        }
        mailSender.send(message);
    }

    public boolean validateOtp(HttpSession session, String phone, String otp) {
        Object codeObj = session.getAttribute(OTP_CODE_KEY);
        Object phoneObj = session.getAttribute(OTP_PHONE_KEY);
        Object expObj = session.getAttribute(OTP_EXPIRES_KEY);
        if (codeObj == null || phoneObj == null || expObj == null) {
            return false;
        }
        if (!phone.equals(phoneObj.toString())) {
            return false;
        }
        if (!otp.equals(codeObj.toString())) {
            return false;
        }
        long expiresAt = Long.parseLong(expObj.toString());
        return System.currentTimeMillis() <= expiresAt;
    }

    public void clearOtp(HttpSession session) {
        session.removeAttribute(OTP_CODE_KEY);
        session.removeAttribute(OTP_PHONE_KEY);
        session.removeAttribute(OTP_EXPIRES_KEY);
    }
}
