package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/setmeal")
@Api("套餐管理")
public class SetMealController {
    @Autowired
    private SetMealService setMealService;

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO queryDTO) {
        PageResult pageResult = setMealService.page(queryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setMealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> findSetMealById(@PathVariable Long id){
        SetmealVO setmealVO = setMealService.findSetMealById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐信息")
    public Result updateSetMeal(@RequestBody SetmealDTO setmealDTO){
        setMealService.updateSetMeal(setmealDTO);
        return Result.success();
    }
}
