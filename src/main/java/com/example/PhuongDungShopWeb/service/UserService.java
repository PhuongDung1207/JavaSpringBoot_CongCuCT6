package com.example.PhuongDungShopWeb.service;

import jakarta.validation.constraints.NotNull; 
import lombok.extern.slf4j.Slf4j; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.core.userdetails.UserDetails; 
import org.springframework.security.core.userdetails.UserDetailsService; 
import org.springframework.security.core.userdetails.UsernameNotFoundException; 
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; 
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;

import com.example.PhuongDungShopWeb.model.RewardVoucher;
import com.example.PhuongDungShopWeb.model.Role;
import com.example.PhuongDungShopWeb.model.User;
import com.example.PhuongDungShopWeb.repository.IRoleRepository;
import com.example.PhuongDungShopWeb.repository.IUserRepository;

import java.util.HashSet;
import java.util.Optional; 
 
@Service 
@Slf4j 
@Transactional 
public class UserService implements UserDetailsService { 
 
    @Autowired 
    private IUserRepository userRepository; 
    @Autowired 
    private IRoleRepository roleRepository; 
 
    // Lưu người dùng mới vào cơ sở dữ liệu sau khi mã hóa mật khẩu. 
    public void save(@NotNull User user) { 
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword())); 
        userRepository.save(user); 
    } 
 
    // Gán vai trò mặc định cho người dùng dựa trên tên người dùng. 
    public void setDefaultRole(String username) { 
        userRepository.findByUsername(username).ifPresentOrElse( 
                user -> { 
                    Role defaultRole = roleRepository.findByName("USER")
                            .orElseGet(() -> {
                                Role role = new Role();
                                role.setName("USER");
                                role.setDescription("Standard user");
                                return roleRepository.save(role);
                            });
                    if (user.getRoles() == null) {
                        user.setRoles(new HashSet<>());
                    }
                    user.getRoles().add(defaultRole);
                    userRepository.save(user); 
                }, 
                () -> { throw new UsernameNotFoundException("User not found"); } 
        );
        } 
 
    // Tải thông tin chi tiết người dùng để xác thực. 
    @Override 
    public UserDetails loadUserByUsername(String username) throws 
UsernameNotFoundException { 
        var user = userRepository.findByUsername(username) 
                .orElseThrow(() -> new UsernameNotFoundException("User not found")); 
        return org.springframework.security.core.userdetails.User 
                .withUsername(user.getUsername()) 
                .password(user.getPassword()) 
                .authorities(user.getAuthorities()) 
                .accountExpired(!user.isAccountNonExpired()) 
                .accountLocked(!user.isAccountNonLocked()) 
                .credentialsExpired(!user.isCredentialsNonExpired()) 
                .disabled(!user.isEnabled()) 
                .build(); 
    } 
 
    // Tìm kiếm người dùng dựa trên tên đăng nhập. 
    public Optional<User> findByUsername(String username) throws 
UsernameNotFoundException { 
        return userRepository.findByUsername(username); 
    } 

    public Optional<User> findByPhone(String phone) {
        if (phone == null || phone.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByPhone(phone);
    }

    public int getRewardPoints(User user) {
        if (user == null || user.getRewardPoints() == null) {
            return 0;
        }
        return user.getRewardPoints();
    }

    public void addPoints(User user, int points) {
        if (user == null || points <= 0) {
            return;
        }
        user.setRewardPoints(getRewardPoints(user) + points);
        userRepository.save(user);
    }

    public boolean hasActiveVoucher(User user) {
        return user != null && user.getVoucherCode() != null && !user.getVoucherCode().isBlank();
    }

    public boolean canRedeemVoucher(User user, RewardVoucher voucher) {
        if (user == null || voucher == null) {
            return false;
        }
        return getRewardPoints(user) >= voucher.getRequiredPoints() && !hasActiveVoucher(user);
    }

    public void redeemVoucher(User user, RewardVoucher voucher) {
        if (!canRedeemVoucher(user, voucher)) {
            throw new IllegalStateException("Not eligible to redeem voucher.");
        }
        user.setRewardPoints(getRewardPoints(user) - voucher.getRequiredPoints());
        user.setVoucherCode(voucher.getCode());
        userRepository.save(user);
    }

    public void clearVoucher(User user) {
        if (user == null) {
            return;
        }
        user.setVoucherCode(null);
        userRepository.save(user);
    }
} 
