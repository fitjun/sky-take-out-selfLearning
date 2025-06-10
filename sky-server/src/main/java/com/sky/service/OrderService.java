package com.sky.service;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderSubmitVO;

import java.time.LocalDateTime;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO order);

    LocalDateTime pay(OrdersPaymentDTO ordersDTO);

    PageResult OrderHistory(Integer page, Integer pageSize, Integer status);

    OrderDetailVO orderDetail(Long id);

    void cancel(Long id);
}
