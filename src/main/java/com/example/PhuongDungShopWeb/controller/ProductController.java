package com.example.PhuongDungShopWeb.controller;

import com.example.PhuongDungShopWeb.model.Product;
import com.example.PhuongDungShopWeb.model.Category;
import com.example.PhuongDungShopWeb.service.ProductService;
import com.example.PhuongDungShopWeb.service.CategoryService;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product-list";
    }

    @GetMapping("/new")
    public String showProductForm(Model model) {
        Product product = new Product();
        product.setCategory(new Category());
        model.addAttribute("product", product);
        List<Category> categories = categoryService.getChildCategories();
        model.addAttribute("categories", categories);
        return "product-form";
    }

    @PostMapping
    public String saveProduct(@ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
        Product saved = productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo sản phẩm \"" + saved.getName() + "\".");
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        model.addAttribute("product", product);
        List<Category> categories = categoryService.getChildCategories();
        model.addAttribute("categories", categories);
        return "product-form";
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
        Product updated = productService.updateProduct(id, product);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật sản phẩm \"" + updated.getName() + "\".");
        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Product existing = productService.getProductById(id);
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá sản phẩm \"" + existing.getName() + "\".");
        return "redirect:/products";
    }

    @GetMapping("/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "product-detail";
    }

    @GetMapping("/category/{categoryId}")
    public String productsByCategory(@PathVariable Long categoryId, Model model) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("activeCategoryId", categoryId);
        return "product-list";
    }
}
