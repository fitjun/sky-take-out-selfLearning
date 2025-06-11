package com.sky.entity;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private Location location;
    private Integer precise;
    private int confidence;   // 置信度
    private int comprehension;// 理解度
    private String level;     // 级别
}
