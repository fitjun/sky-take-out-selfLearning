package com.sky.controller.user;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public Result<OrderPaymentVO> pay(@RequestBody OrdersDTO ordersDTO) {
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
}
