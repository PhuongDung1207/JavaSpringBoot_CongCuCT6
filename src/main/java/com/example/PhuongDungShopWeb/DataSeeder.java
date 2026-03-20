package com.example.PhuongDungShopWeb;

import com.example.PhuongDungShopWeb.model.RedeemedVoucher;
import com.example.PhuongDungShopWeb.model.Role;
import com.example.PhuongDungShopWeb.model.User;
import com.example.PhuongDungShopWeb.model.VoucherStatus;
import com.example.PhuongDungShopWeb.repository.IRoleRepository;
import com.example.PhuongDungShopWeb.repository.IUserRepository;
import com.example.PhuongDungShopWeb.repository.RedeemedVoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final RedeemedVoucherRepository redeemedVoucherRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            Role adminRole = ensureRole("ADMIN", "System admin");
            Role userRole = ensureRole("USER", "Standard user");
            Role managerRole = ensureRole("MANAGER", "Product manager");

            ensureUser("admin", "admin123", "admin@shop.local", "0900000001", adminRole, 200, null);
            ensureUser("user", "user123", "user@shop.local", "0900000002", userRole, 150, "TGDDSALE50");
            ensureUser("manager", "manager123", "manager@shop.local", "0900000003", managerRole, 50, null);

            seedVouchers();
        };
    }

    private Role ensureRole(String name, String description) {
        var roles = roleRepository.findAllByName(name);
        if (!roles.isEmpty()) {
            return roles.get(0);
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    private void ensureUser(String username, String rawPassword, String email, String phone, Role role,
                            int rewardPoints, String voucherCode) {
        Optional<User> existing = userRepository.findByUsername(username);
        User user = existing.orElseGet(User::new);
        if (existing.isEmpty()) {
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEmail(email);
            user.setPhone(phone);
            user.setRewardPoints(rewardPoints);
            user.setVoucherCode(voucherCode);
        }
        user.getRoles().add(role);
        userRepository.save(user);
    }

    private void seedVouchers() {
        if (redeemedVoucherRepository.count() > 0) {
            return;
        }
        RedeemedVoucher v1 = new RedeemedVoucher();
        v1.setPhone("0900000002");
        v1.setVoucherCode("TGDDSALE50");
        v1.setStatus(VoucherStatus.UNUSED);
        v1.setRedeemedAt(LocalDateTime.now());

        RedeemedVoucher v2 = new RedeemedVoucher();
        v2.setPhone("0900000002");
        v2.setVoucherCode("TGDDSALE100");
        v2.setStatus(VoucherStatus.UNUSED);
        v2.setRedeemedAt(LocalDateTime.now());

        RedeemedVoucher v3 = new RedeemedVoucher();
        v3.setPhone("0900000003");
        v3.setVoucherCode("TGDDSALE150");
        v3.setStatus(VoucherStatus.UNUSED);
        v3.setRedeemedAt(LocalDateTime.now());

        redeemedVoucherRepository.save(v1);
        redeemedVoucherRepository.save(v2);
        redeemedVoucherRepository.save(v3);
    }
}
