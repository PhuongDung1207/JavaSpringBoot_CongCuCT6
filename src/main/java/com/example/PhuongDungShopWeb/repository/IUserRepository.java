package com.example.PhuongDungShopWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;
import com.example.PhuongDungShopWeb.model.User;

import java.util.Optional; 
@Repository 
public interface IUserRepository extends JpaRepository<User, Long> { 
    Optional<User> findByUsername(String username); 
    Optional<User> findByPhone(String phone); 
} 
