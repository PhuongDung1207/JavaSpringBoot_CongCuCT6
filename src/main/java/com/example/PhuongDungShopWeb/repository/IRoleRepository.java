package com.example.PhuongDungShopWeb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.PhuongDungShopWeb.model.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleRepository extends JpaRepository<Role, Long>{
    Role findRoleById(Long id); 
    Optional<Role> findByName(String name);
    List<Role> findAllByName(String name);
}
