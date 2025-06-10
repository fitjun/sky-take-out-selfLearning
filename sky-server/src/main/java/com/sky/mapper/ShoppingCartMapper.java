package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {
    List<ShoppingCart> findShoppingCart(ShoppingCart shoppingCart);

    void add(ShoppingCart shoppingCart);

    void update(ShoppingCart sc);
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void clean(Long userId);
    @Delete("delete from shopping_cart where id = #{id}")
    void delById(Long id);

    void addAll(List<ShoppingCart> shoppingCarts);
}
