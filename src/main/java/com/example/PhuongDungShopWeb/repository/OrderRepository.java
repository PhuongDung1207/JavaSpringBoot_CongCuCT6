package com.example.PhuongDungShopWeb.repository;

import com.example.PhuongDungShopWeb.model.Order; 
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository; 
@Repository 
public interface OrderRepository extends JpaRepository<Order, Long> { 
}
