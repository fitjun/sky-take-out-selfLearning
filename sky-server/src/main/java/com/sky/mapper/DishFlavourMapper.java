package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface DishFlavourMapper {
    void addFlavour(List<DishFlavor> flavors);

    void delByDishId(Long[] ids);
    @Select("select * from dish_flavor where dish_id = #{id}")
    List<DishFlavor> findByDishId(Long id);

}
