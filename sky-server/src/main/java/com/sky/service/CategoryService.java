package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    PageResult page(CategoryPageQueryDTO category);

    void updateCategory(CategoryDTO category);

    void changeStatus(Integer status,Long id);

    void addCatagory(CategoryDTO categoryDTO);

    List<Category> listByType(Integer type);

    void delCatagoryById(Long id);
}
