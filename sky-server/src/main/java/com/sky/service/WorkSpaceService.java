package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {
    BusinessDataVO businessData(LocalDateTime start, LocalDateTime end);

    SetmealOverViewVO setmealData();

    DishOverViewVO dishData();

    OrderOverViewVO orderData();
}
