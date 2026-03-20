package com.example.PhuongDungShopWeb.service;

import com.example.PhuongDungShopWeb.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoService {

    private final String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private final String partnerCode = "MOMOBKUN20180529";
    private final String accessKey = "klm05TvNBzhg7h7j";
    private final String secretKey = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";

    public String createPayment(Order order, String host) throws Exception {
        String orderId = String.valueOf(order.getId()) + "_" + System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString();
        String amount = String.valueOf((long) order.getTotalAmount());
        String orderInfo = "Thanh toan don hang #" + order.getId();
        String returnUrl = host + "/order/momo-return";
        String notifyUrl = host + "/order/momo-notify"; // IPN URL
        String requestType = "captureWallet";
        String extraData = "";

        // Raw signature string
        String rawSignature = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + notifyUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + returnUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        // Hash signature
        String signature = hmacSHA256(rawSignature, secretKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("partnerCode", partnerCode);
        requestBody.put("partnerName", "Test Store");
        requestBody.put("storeId", "MomoTestStore");
        requestBody.put("requestId", requestId);
        requestBody.put("amount", amount);
        requestBody.put("orderId", orderId);
        requestBody.put("orderInfo", orderInfo);
        requestBody.put("redirectUrl", returnUrl);
        requestBody.put("ipnUrl", notifyUrl);
        requestBody.put("lang", "vi");
        requestBody.put("extraData", extraData);
        requestBody.put("requestType", requestType);
        requestBody.put("signature", signature);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json; charset=UTF-8");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(endpoint, entity, Map.class);
        
        if (response != null && response.containsKey("payUrl")) {
            return (String) response.get("payUrl");
        }

        throw new RuntimeException("Giao dịch MoMo thất bại. Phản hồi: " + response);
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
