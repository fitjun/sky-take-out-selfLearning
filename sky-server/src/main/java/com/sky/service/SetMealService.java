package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetMealService {
    PageResult page(SetmealPageQueryDTO queryDTO);

    void save(SetmealDTO setmealDTO);

    SetmealVO findSetMealById(Long id);

    void updateSetMeal(SetmealDTO setmealDTO);

    void changeStatus(Integer status,Long id);

    void delSetmealById(Long[] ids);

    List<Setmeal> findSetmealByCategoryId(Integer categoryId);

    List<Dish> findDishBySetmealId(Integer semealId);
}
