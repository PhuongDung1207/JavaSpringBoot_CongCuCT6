package com.example.PhuongDungShopWeb.controller;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.*;

import com.example.PhuongDungShopWeb.service.CartService; 
 
@Controller 
@RequestMapping("/cart") 
public class CartController { 
 
    @Autowired 
    private CartService cartService; 
 
    @GetMapping 
    public String showCart() { 
        return "redirect:/order/checkout"; 
    } 
 
    @PostMapping("/add") 
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) { 
            cartService.addToCart(productId, quantity);
            return "redirect:/order/checkout"; 
    } 
    @GetMapping("/remove/{productId}") 
    public String removeFromCart(@PathVariable Long productId) { 
        cartService.removeFromCart(productId); 
        return "redirect:/order/checkout"; 
    } 
    @GetMapping("/clear") 
    public String clearCart() { 
        cartService.clearCart(); 
        return "redirect:/order/checkout"; 
    } 
}
