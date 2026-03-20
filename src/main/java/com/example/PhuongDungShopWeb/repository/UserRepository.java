package com.example.PhuongDungShopWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import com.example.PhuongDungShopWeb.model.User;

public interface UserRepository extends JpaRepository<User, Long> { 
    User findByUsername(String username); 
}
