package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.vo.OrderSubmitVO;

import java.time.LocalDateTime;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO order);

    LocalDateTime pay(OrdersDTO ordersDTO);
}
