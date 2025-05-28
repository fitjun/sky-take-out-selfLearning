package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealDishMapper {
    @Select("select id from setmeal_dish where dish_id = #{id}")
    Long findByDishId(Long id);

    void addMealDish(List<SetmealDish> setmealDishes);

    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> findBySetMealId(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void delBySetmealId(Long id);
}
