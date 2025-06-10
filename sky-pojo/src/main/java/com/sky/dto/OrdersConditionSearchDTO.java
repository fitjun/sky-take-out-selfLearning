package com.sky.dto;

import com.sky.entity.Orders;
import com.sun.org.apache.xpath.internal.operations.Or;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersConditionSearchDTO extends Orders {
    //@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:SS")
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private Integer page;
    private Integer pageSize;
}
