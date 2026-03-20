package com.example.PhuongDungShopWeb.controller;

import com.example.PhuongDungShopWeb.model.Category;
import com.example.PhuongDungShopWeb.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categories/parents")
    public String parentsList(Model model) {
        model.addAttribute("parents", categoryService.getParentCategories());
        return "/categories/parents-list";
    }

    @GetMapping("/categories/parents/add")
    public String addParentForm(Model model) {
        model.addAttribute("category", new Category());
        return "/categories/parent-form";
    }

    @PostMapping("/categories/parents/add")
    public String addParent(@Valid Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/categories/parent-form";
        }
        category.setParent(null);
        categoryService.addCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo danh mục cha \"" + category.getName() + "\".");
        return "redirect:/categories/parents";
    }

    @GetMapping("/categories/parents/edit/{id}")
    public String editParentForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElseThrow();
        category.setParent(null);
        model.addAttribute("category", category);
        return "/categories/parent-form";
    }

    @PostMapping("/categories/parents/update/{id}")
    public String updateParent(@PathVariable Long id, @Valid Category category, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "/categories/parent-form";
        }
        category.setId(id);
        category.setParent(null);
        categoryService.updateCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật danh mục cha \"" + category.getName() + "\".");
        return "redirect:/categories/parents";
    }

    @GetMapping("/categories/parents/delete/{id}")
    public String deleteParent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Category existing = categoryService.getCategoryById(id).orElseThrow();
        categoryService.deleteCategoryById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá danh mục cha \"" + existing.getName() + "\".");
        return "redirect:/categories/parents";
    }

    @GetMapping("/categories/children")
    public String childrenList(Model model) {
        model.addAttribute("children", categoryService.getChildCategories());
        return "/categories/children-list";
    }

    @GetMapping("/categories/children/add")
    public String addChildForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parents", categoryService.getParentCategories());
        return "/categories/child-form";
    }

    @PostMapping("/categories/children/add")
    public String addChild(@Valid Category category, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("parents", categoryService.getParentCategories());
            return "/categories/child-form";
        }
        categoryService.addCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo danh mục con \"" + category.getName() + "\".");
        return "redirect:/categories/children";
    }

    @GetMapping("/categories/children/edit/{id}")
    public String editChildForm(@PathVariable Long id, Model model) {
        Category category = categoryService.getCategoryById(id).orElseThrow();
        model.addAttribute("category", category);
        model.addAttribute("parents", categoryService.getParentCategories());
        return "/categories/child-form";
    }

    @PostMapping("/categories/children/update/{id}")
    public String updateChild(@PathVariable Long id, @Valid Category category, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("parents", categoryService.getParentCategories());
            return "/categories/child-form";
        }
        category.setId(id);
        categoryService.updateCategory(category);
        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật danh mục con \"" + category.getName() + "\".");
        return "redirect:/categories/children";
    }

    @GetMapping("/categories/children/delete/{id}")
    public String deleteChild(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Category existing = categoryService.getCategoryById(id).orElseThrow();
        categoryService.deleteCategoryById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xoá danh mục con \"" + existing.getName() + "\".");
        return "redirect:/categories/children";
    }
}
