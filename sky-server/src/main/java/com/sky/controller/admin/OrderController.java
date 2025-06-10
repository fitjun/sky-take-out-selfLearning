package com.sky.controller.admin;

import com.sky.dto.OrdersConditionSearchDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/admin/order")
@Api(tags = "管理端订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderDetailVO> OrderDetail(@PathVariable Long id){
        OrderDetailVO orderDetailVO = orderService.orderDetail(id);
        return Result.success(orderDetailVO);
    }
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> searchOrder(Integer page, Integer pageSize, LocalDateTime beginTime, LocalDateTime endTime, String number,String phone,Integer status){
        PageResult result= orderService.ConditionSearch(page,pageSize,beginTime,endTime,number,phone,status);
        return Result.success(result);
    }
}
