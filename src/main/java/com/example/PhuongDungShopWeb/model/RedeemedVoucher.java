package com.example.PhuongDungShopWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "redeemed_voucher")
public class RedeemedVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String phone;

    @Column(length = 50, nullable = false)
    private String voucherCode;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private VoucherStatus status;

    @Column(nullable = false)
    private LocalDateTime redeemedAt;

    private LocalDateTime usedAt;

    private Long orderId;

    @PrePersist
    public void prePersist() {
        if (redeemedAt == null) {
            redeemedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = VoucherStatus.UNUSED;
        }
    }
}
