package com.sky.service;
import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.vo.OrderDetailVO;
import com.sky.vo.OrderStatistusVO;
import com.sky.vo.OrderSubmitVO;

import java.time.LocalDateTime;

public interface OrderService {
    OrderSubmitVO submit(OrdersSubmitDTO order);

    LocalDateTime pay(OrdersPaymentDTO ordersDTO);

    PageResult OrderHistory(Integer page, Integer pageSize, Integer status);

    OrderDetailVO orderDetail(Long id);

    void cancel(OrdersCancelDTO cancelDTO);

    void repetition(Long id);

    PageResult ConditionSearch(Integer page, Integer pageSize, LocalDateTime beginTime, LocalDateTime endTime, String number, String phone, Integer status);

    OrderStatistusVO orderStatics();

    void confirm(Long id);

    void rejection(OrdersRejectionDTO rejectionDTO);

    void delivery(Long id);

    void complete(Long id);
}
