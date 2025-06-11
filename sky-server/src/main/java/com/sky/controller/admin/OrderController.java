package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderStatistusVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

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
    public Result<PageResult> searchOrder(Integer page, Integer pageSize,
                                          //一定要将完整的时间转换格式写出（包含时分秒）否则报错无法转换
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime beginTime,
                                          @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                          String number,String phone,Integer status){
        PageResult result= orderService.ConditionSearch(page,pageSize,beginTime,endTime,number,phone,status);
        return Result.success(result);
    }
    @GetMapping("/statistics")
    @ApiOperation("订单状态数量统计")
    public Result<OrderStatistusVO> OrderStatics(){
        OrderStatistusVO orderStatistusVO = orderService.orderStatics();
        return Result.success(orderStatistusVO);
    }
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        orderService.confirm(ordersConfirmDTO.getId());
        return Result.success();
    }
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result reject(@RequestBody OrdersRejectionDTO rejectionDTO){
        orderService.rejection(rejectionDTO);
        return Result.success();
    }
    @PutMapping("/cancel")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        orderService.delivery(id);
        return Result.success();
    }
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable Long id){
        orderService.complete(id);
        return Result.success();
    }
}
