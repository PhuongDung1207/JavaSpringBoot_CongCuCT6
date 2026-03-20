package com.example.PhuongDungShopWeb.controller;

import com.example.PhuongDungShopWeb.model.PromotionType;
import com.example.PhuongDungShopWeb.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;

    public HomeController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("nonePromotionProducts", productService.getProductsByPromotionType(PromotionType.NONE));
        model.addAttribute("discountProducts", productService.getProductsByPromotionType(PromotionType.DISCOUNT));
        model.addAttribute("giftProducts", productService.getProductsByPromotionType(PromotionType.GIFT));
        return "index";
    }
}
