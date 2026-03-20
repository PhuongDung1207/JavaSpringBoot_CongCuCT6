package com.example.PhuongDungShopWeb.web;

import com.example.PhuongDungShopWeb.model.Category;
import com.example.PhuongDungShopWeb.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConverter implements Converter<String, Category> {
    private final CategoryService categoryService;

    @Override
    public Category convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            Long id = Long.valueOf(source);
            return categoryService.getCategoryById(id).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
