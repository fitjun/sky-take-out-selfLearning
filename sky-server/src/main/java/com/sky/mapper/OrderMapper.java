package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrderStatusDTO;
import com.sky.dto.OrdersConditionSearchDTO;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders o);

    void insertOrderDetail(List<OrderDetail> ol);

    void update(Orders orders);

    Page<OrderVO> findOrder(OrdersConditionSearchDTO orders);

    List<OrderDetail> findOrderDetailByOrderId(Long id);
    @Select("select * from orders where id =#{id}")
    Orders findOrderById(Long id);

    void cancel(Orders orders);

    List<OrderStatusDTO> StaticCount();
    @Select("select * from orders where status=#{status} and checkout_time < #{time}")
    List<Orders> getByStatusAndOrderTimeLT(Integer status , LocalDateTime time);
}
