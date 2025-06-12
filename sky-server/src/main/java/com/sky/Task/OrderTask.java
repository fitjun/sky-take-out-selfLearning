package com.sky.Task;

import com.github.pagehelper.Page;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * *")
//    @Scheduled(cron = "1/5 * * * * *")
    public void processTimeoutOrder() {
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        //查询超时订单，并设置为取消15分钟没接单就算超时
        LocalDateTime time = LocalDateTime.now().minusMinutes(15);
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.TO_BE_CONFIRMED, time);
        if(byStatusAndOrderTimeLT!=null && byStatusAndOrderTimeLT.size()>0) {
            byStatusAndOrderTimeLT.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时、自动取消");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
//    @Scheduled(cron = "0/5 * * * * ?")
    public void processDeliveryOrder(){
        log.info("自动处理一直正在派送中的订单：{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().minusMinutes(60);//查询前一天的一直在派送的订单
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, time);
        if(byStatusAndOrderTimeLT!=null && byStatusAndOrderTimeLT.size()>0){
            byStatusAndOrderTimeLT.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            });
        }

    }
}
