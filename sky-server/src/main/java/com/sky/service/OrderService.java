package com.sky.service;
import com.sky.dto.OrdersConditionSearchDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderSubmitVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO order);

    LocalDateTime pay(OrdersPaymentDTO ordersDTO);

    PageResult OrderHistory(Integer page, Integer pageSize, Integer status);

    OrderDetailVO orderDetail(Long id);

    void cancel(Long id);

    void repetition(Long id);

    PageResult ConditionSearch(Integer page, Integer pageSize, LocalDateTime beginTime, LocalDateTime endTime, String number, String phone, Integer status);
}
