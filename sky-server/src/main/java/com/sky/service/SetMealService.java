package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

public interface SetMealService {
    PageResult page(SetmealPageQueryDTO queryDTO);

    void save(SetmealDTO setmealDTO);

    SetmealVO findSetMealById(Long id);

    void updateSetMeal(SetmealDTO setmealDTO);
}
