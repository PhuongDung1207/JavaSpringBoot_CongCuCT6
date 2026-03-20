package com.example.PhuongDungShopWeb.model;

public class RewardVoucher {
    private final String code;
    private final String title;
    private final int requiredPoints;
    private final double discountAmount;

    public RewardVoucher(String code, String title, int requiredPoints, double discountAmount) {
        this.code = code;
        this.title = title;
        this.requiredPoints = requiredPoints;
        this.discountAmount = discountAmount;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }
}
