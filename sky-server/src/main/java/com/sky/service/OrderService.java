package com.sky.service;

import com.sky.dto.OrdersDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO order);

    LocalDateTime pay(OrdersDTO ordersDTO);

    PageResult OrderHistory(Integer page, Integer pageSize, Integer status);
}
