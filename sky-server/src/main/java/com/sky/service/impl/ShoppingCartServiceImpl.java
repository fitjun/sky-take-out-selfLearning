package com.sky.service.impl;

import com.sky.annotation.AutoFill;
import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public List<ShoppingCart> findAll() {
        ShoppingCart shoppingCart = new ShoppingCart();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.findShoppingCart(shoppingCart);
        return shoppingCarts;
    }

    @Override
    public void add(ShoppingCartDTO shoppingCartDTO) {
        //判断商品数量，如果为0则插入，如果已经有，则number增加即可
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //因为是传入了dishId，setmealId等，所以这里查出来必定是用户要修改的那个购物车数据，且只有一条。所以不用遍历，只需要将那一条修改就好
        List<ShoppingCart> shoppingCart1 = shoppingCartMapper.findShoppingCart(shoppingCart);
        if (shoppingCart1 != null && shoppingCart1.size() > 0) {
            ShoppingCart sc = shoppingCart1.get(0);
            sc.setNumber(sc.getNumber() + 1);
            shoppingCartMapper.update(sc);
        } else {
            //购物车没有，插入
            Long setmealId = shoppingCartDTO.getSetmealId();
            Long dishId = shoppingCartDTO.getDishId();
            if (setmealId!=null){
                //加入的是套餐，查询套餐信息
                log.info("用户网购物车加入套餐");
                SetmealVO setmealVO = setMealMapper.findById(setmealId);
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setNumber(1);
                BeanUtils.copyProperties(setmealVO, shoppingCart);
                shoppingCartMapper.add(shoppingCart);
            }else {
                log.info("用户购物车加入单菜品");
                Dish dish = dishMapper.findById(dishId);
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                BeanUtils.copyProperties(dish, shoppingCart);
                shoppingCartMapper.add(shoppingCart);
            }
        }
    }

    @Override
    public void clean() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    @Override
    public void subItem(ShoppingCartDTO shoppingCartDTO) {
        //查询购物车，找到该条数据，若不存在抛出异常 若减后为0则删除该条数据
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        List<ShoppingCart> items = shoppingCartMapper.findShoppingCart(shoppingCart);
        if (items != null && items.size() > 0) {
            ShoppingCart shoppingCart1 = items.get(0);
            Integer number = shoppingCart1.getNumber();
            if (number>1){
                shoppingCart1.setNumber(number-1);
                shoppingCartMapper.update(shoppingCart1);
            }else {
                shoppingCartMapper.delById(shoppingCart1.getId());
            }
        }else {
            throw new DeletionNotAllowedException("该商品不存在购物车");
        }
    }
}
