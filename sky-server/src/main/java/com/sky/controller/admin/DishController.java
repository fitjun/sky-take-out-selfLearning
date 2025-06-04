package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result addDish(@RequestBody DishDTO dish) {
        dishService.addDishWithFlavour(dish);
        redisTemplate.delete("DISH:" + dish.getCategoryId());
        return Result.success();
    }
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page( DishPageQueryDTO queryDTO) {
        PageResult result = dishService.page(queryDTO);
        return Result.success(result);
    }

    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delDish(Long[]ids){
        dishService.delDish(ids);
        ClearCache("DISH:*");
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("改变菜品状态")
    public Result ChangeStatus(@PathVariable Integer status,Long id){
        dishService.ChangeStatus(status,id);
        ClearCache("DISH:*");
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品及口味")
    public Result<DishVO> findDishAndFlavorById(@PathVariable Long id){
        DishVO dishVO = dishService.findDishAndFlavorById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品")
    public Result updateDish(@RequestBody DishDTO dishDTO){
        dishService.updateDish(dishDTO);
        ClearCache("DISH:*");
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> findByCatagoryId(@RequestParam Long categoryId){
        List<DishVO> dishes= dishService.findBycatagoryId(categoryId);
        return Result.success(dishes);
    }

    private void ClearCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
