package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController("UserDishController")
@RequestMapping("/user/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    public Result<List<DishVO>> findByCategoryId(@RequestParam Long categoryId) {
        List<DishVO> dishes = dishService.findBycatagoryId(categoryId);
        //停售的不展示给客户端
        List<DishVO> collect = dishes.stream()
                .filter(dish -> dish.getStatus() != StatusConstant.DISABLE)
                .collect(Collectors.toList());
        return Result.success(collect);
    }
}
