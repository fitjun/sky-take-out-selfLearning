package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public BusinessDataVO businessData() {
        //返回数据有当天总订单数、当天有效订单数、当天新顾客、当天营业额
        Map map = new HashMap();
        LocalDate localDate = LocalDate.now();
        map.put("begin",LocalDateTime.of(localDate,LocalTime.MIN));
        map.put("end",LocalDateTime.of(localDate,LocalTime.MAX));
        map.put("dayStart",LocalDateTime.of(localDate,LocalTime.MIN));
        map.put("dayEnd",LocalDateTime.of(localDate,LocalTime.MAX));
        map.put("startTime",LocalDateTime.of(localDate,LocalTime.MIN));
        map.put("endTime",LocalDateTime.of(localDate,LocalTime.MAX));
        Integer newUsers = userMapper.CountUser(map);
        if (newUsers==null){
            newUsers = 0;
        }
        Integer orders = orderMapper.countOrders(map);
        if (orders == null){
            orders = 0;
        }
        map.put("status", Orders.COMPLETED);
        Integer validOrders = orderMapper.countOrders(map);
        if (validOrders == null){
            validOrders = 0;
        }
        Double turnover = orderMapper.turnOver(map);
        if (turnover == null){
            turnover =0.0;
        }
        return BusinessDataVO.builder()
                .orderCompletionRate(orders==0?0.0:validOrders.doubleValue()/orders)
                .unitPrice(orders==0?0.0:turnover.doubleValue()/validOrders)
                .newUsers(newUsers)
                .validOrderCount(validOrders)
                .turnover(turnover)
                .build();
    }
}
