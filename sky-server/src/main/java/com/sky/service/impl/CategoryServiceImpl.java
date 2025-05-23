package com.sky.service.impl;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Override
    public PageResult page(CategoryPageQueryDTO category) {
        PageHelper.startPage(category.getPage(),category.getPageSize());
        Page<Category> page = categoryMapper.findCategory(category);
        long total = page.getTotal();
        List<Category> result = page.getResult();
        return new PageResult(total,result);
    }

    @Override
    public void updateCategory(CategoryDTO category) {
        Category categoryEntity = new Category();
        BeanUtils.copyProperties(category,categoryEntity);
        categoryEntity.setUpdateTime(LocalDateTime.now());
        categoryEntity.setUpdateUser(BaseContext.getCurrentId());
        categoryMapper.update(categoryEntity);
    }

    @Override
    public void changeStatus(Integer status,Long id) {
        Category category = new Category();
        category.setStatus(status);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setId(id);
        categoryMapper.update(category);
    }

    @Override
    public void addCatagory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        category.setCreateTime(LocalDateTime.now());
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setStatus(1);
        categoryMapper.addCatagory(category);
    }

    @Override
    public List<Category> listByType(Integer type) {
        List<Category> categories = categoryMapper.listByType(type);
        return categories;
    }
}
