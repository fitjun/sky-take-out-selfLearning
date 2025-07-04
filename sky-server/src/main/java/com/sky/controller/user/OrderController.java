package com.sky.controller.user;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.OrderDetail;
import com.sky.properties.ShopProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.utils.DistanceUtil;
import com.sky.vo.*;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Api(tags = "/订单接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WebSocketServer webSocketServer;

    @PostMapping("/submit")
    @ApiOperation("提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO order) {
        OrderSubmitVO submit = orderService.submit(order);
        if (submit == null){
            return Result.error("距离商铺大于5公里，请选择其他商铺");
        }
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
        OrdersCancelDTO o = new OrdersCancelDTO();
        o.setId(id);
        orderService.cancel(o);
        return Result.success();
    }
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id){
        orderService.reminder(id);
        return Result.success();
    }
}
