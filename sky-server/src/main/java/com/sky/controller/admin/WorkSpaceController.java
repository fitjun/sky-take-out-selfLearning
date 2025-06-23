package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/workspace")
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData(){
        BusinessDataVO vo = workSpaceService.businessData();
        return Result.success(vo);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> setmealData(){
        SetmealOverViewVO vo = workSpaceService.setmealData();
        return Result.success(vo);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> dishData(){
        DishOverViewVO vo = workSpaceService.dishData();
        return Result.success(vo);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> orderData(){
        OrderOverViewVO vo = workSpaceService.orderData();
        return Result.success(vo);
    }
}
