package com.example.PhuongDungShopWeb.web;

import com.example.PhuongDungShopWeb.model.Category;
import com.example.PhuongDungShopWeb.service.CartService;
import com.example.PhuongDungShopWeb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {
    private final CategoryService categoryService;
    private final CartService cartService;

    @ModelAttribute("parentCategories")
    public List<Category> parentCategories() {
        return categoryService.getParentCategories();
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return cartService.getTotalQuantity();
    }
}
