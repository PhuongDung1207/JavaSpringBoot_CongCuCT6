package com.example.PhuongDungShopWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;
import com.example.PhuongDungShopWeb.model.OrderDetail; 

@Repository 
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> { 
} 
