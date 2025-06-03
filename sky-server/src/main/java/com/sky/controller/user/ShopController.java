package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController(value = "userShopController")
@RequestMapping("/user/shop")
@Api(tags = "用户端商店管理")
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
}
