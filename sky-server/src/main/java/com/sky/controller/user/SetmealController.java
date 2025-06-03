package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserSetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "用户套餐相关接口")
public class SetmealController {
    @Autowired
    private SetMealService setMealService;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<Setmeal>> findByCatagoryId(Integer categoryId) {
        List<Setmeal> setmeals = setMealService.findSetmealByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询套餐内菜品")
    public Result<List<Dish>> findDishBySetmealId(@PathVariable Integer id) {
        List<Dish> dishes = setMealService.findDishBySetmealId(id);
        return Result.success(dishes);
    }
}
