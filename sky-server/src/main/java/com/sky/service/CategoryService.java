package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.result.PageResult;

public interface CategoryService {

    PageResult page(CategoryPageQueryDTO category);

    void updateCategory(CategoryDTO category);
}
