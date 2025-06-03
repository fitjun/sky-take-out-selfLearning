package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    void addDishWithFlavour(DishDTO dish);

    PageResult page(DishPageQueryDTO queryDTO);

    void delDish(Long[] ids);

    void ChangeStatus(Integer status, Long id);

    DishVO findDishAndFlavorById(Long id);

    void updateDish(DishDTO dishDTO);

    List<DishVO> findBycatagoryId(Long id);
}
