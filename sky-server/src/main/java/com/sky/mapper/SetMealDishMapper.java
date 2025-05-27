package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetMealDishMapper {
    @Select("select id from setmeal_dish where dish_id = #{id}")
    Long findByDishId(Long id);
}
