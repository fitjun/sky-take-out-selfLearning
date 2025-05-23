package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    @ApiOperation("品类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO category) {
        PageResult res = categoryService.page(category);
        return Result.success(res);
    }

    @PutMapping
    @ApiOperation("修改分类")
    public Result updateCategory(@RequestBody CategoryDTO category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用分类")
    //明确了地址栏需要的变量才是@PathVariable，通过问好后传输的变量用RequestParam
    public Result changeStatus(@PathVariable Integer status,@RequestParam Long id){
        categoryService.changeStatus(status,id);
        return Result.success();
    }

    @PostMapping
    @ApiOperation("新增分类")
    public Result addCatagory(@RequestBody CategoryDTO categoryDTO){
        categoryService.addCatagory(categoryDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("根据类型查询分类")
    public Result<List<Category>> listByType(@RequestParam(required = false) Integer type){
        List<Category> categories = categoryService.listByType(type);
        return Result.success(categories);
    }
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result delCatagoryById(@RequestParam Long id){
        categoryService.delCatagoryById(id);
        return Result.success();
    }
}
