package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shop")
@Api(tags = "店铺管理")
@Slf4j
public class ShopController {
    public static final String KEY = "shop_status";
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("获取营业状态")
    public Result<Integer> getShopStatus(){
        Integer o =Integer.valueOf((String) redisTemplate.opsForValue().get(KEY));
        log.info("获取营业状态为:{}",o==1?"营业中":"打烊中");
        return Result.success(o);
    }

    @PutMapping("/{status}")
    @ApiOperation("设置营业状态")
    public Result setShopStatus(@PathVariable("status") Integer status){
        log.info("设置获取营业状态为:{}",status==1?"营业中":"打烊中");
        redisTemplate.opsForValue().set(KEY,status+"");
        return Result.success();
    }
}
