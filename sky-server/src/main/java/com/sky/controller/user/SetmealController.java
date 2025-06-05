package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "用户套餐相关接口")
public class SetmealController {
    @Autowired
    private SetMealService setMealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmealCache", key = "#categoryId")
    public Result<List<Setmeal>> findByCatagoryId(Integer categoryId) {
        List<Setmeal> setmeals = setMealService.findSetmealByCategoryId(categoryId);
        List<Setmeal> collects = setmeals.stream()
                .filter(setmeal -> setmeal.getStatus() == StatusConstant.ENABLE)
                .collect(Collectors.toList());
        return Result.success(collects);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询套餐内菜品")
    public Result<List<Dish>> findDishBySetmealId(@PathVariable Integer id) {
        List<Dish> dishes = setMealService.findDishBySetmealId(id);
        return Result.success(dishes);
    }
}
