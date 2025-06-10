package com.sky.vo;

import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import io.swagger.annotations.ApiModel;
import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "订单详情返回数据")
public class OrderDetailVO{
    private String address;
    private Long addressBookId;
    private BigDecimal amount;
    private String cancelReason;
    private LocalDateTime cancelTime;
    private LocalDateTime checkOutTime;
    private String consignee;
    private Integer deliveryStatus;
    private String deliveryTime;
    private LocalDateTime estimatedDeliveryTime;
    private Long id;
    private String number;
    private List<OrderDetail> orderDetailList;
    private LocalDateTime orderTime;
    private int packAmount;
    private Integer payStatus;
    private String phone;
    private String rejectionReason;
    private String remark;
    private Integer status;
    private Integer tablewareNumber;
    private Integer tablewareStatus;
    private String userName;
    private Long userId;
}
