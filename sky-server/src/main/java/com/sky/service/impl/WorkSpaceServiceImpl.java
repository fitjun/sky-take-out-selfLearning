package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.models.auth.In;
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
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public BusinessDataVO businessData() {
        //返回数据有当天总订单数、当天有效订单数、当天新顾客、当天营业额
        Map map = new HashMap();
        LocalDate localDate = LocalDate.now();
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

    @Override
    public SetmealOverViewVO setmealData() {
        Integer sold = setMealMapper.countStatus(1);
        Integer discontinued = setMealMapper.countStatus(0);
        return SetmealOverViewVO.builder()
                .sold(sold==null?0:sold)
                .discontinued(discontinued==null?0:discontinued)
                .build();
    }

    @Override
    public DishOverViewVO dishData() {
        Integer sold = dishMapper.countStatus(1);
        Integer discontinued = dishMapper.countStatus(0);
        return DishOverViewVO.builder()
                .sold(sold==null?0:sold)
                .discontinued(discontinued==null?0:discontinued)
                .build();
    }

    @Override
    public OrderOverViewVO orderData() {
        Map map = new HashMap();
        LocalDate localDate = LocalDate.now();
        LocalDateTime begin = LocalDateTime.of(localDate,LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(localDate,LocalTime.MAX);
        map.put("startTime",begin);
        map.put("endTime",end);
        Integer allOrders = orderMapper.countOrders(map);
        map.put("status",Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countOrders(map);
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.countOrders(map);
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countOrders(map);
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countOrders(map);
        return OrderOverViewVO.builder()
                .deliveredOrders(deliveredOrders==null?0:deliveredOrders)
                .allOrders(allOrders==null?0:allOrders)
                .cancelledOrders(cancelledOrders==null?0:cancelledOrders)
                .completedOrders(completedOrders==null?0:completedOrders)
                .waitingOrders(waitingOrders==null?0:waitingOrders)
                .build();
    }
}
