package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/user/order")
@Api(tags = "/订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO order) {
        OrderSubmitVO submit = orderService.submit(order);
        return Result.success(submit);
    }

    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> pay(@RequestBody OrdersPaymentDTO ordersDTO) {
        orderService.pay(ordersDTO);
        OrderPaymentVO paymentVO = OrderPaymentVO.builder()
                .packageStr("prepay_id=wx1234567890")
                .paySign("paySign")
                .timeStamp("1640629000")
                .nonceStr("1640629000")
                .signType("RSA")
                .build();
        return Result.success(paymentVO);
    }

    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult>historyOrders(Integer page, Integer pageSize, Integer status) {
        PageResult result = orderService.OrderHistory(page,pageSize,status);
        return Result.success(result);
    }

    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查看订单详情")
    public Result<OrderDetailVO> orderDetail(@PathVariable Long id){
        OrderDetailVO detail = orderService.orderDetail(id);
        return Result.success(detail);
    }

    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id){
        orderService.cancel(id);
        return Result.success();
    }
}
