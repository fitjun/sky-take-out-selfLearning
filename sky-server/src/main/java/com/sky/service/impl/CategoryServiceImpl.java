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
}
