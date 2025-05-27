package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    @AutoFill(value = OperationType.INSERT)
    void addDish(Dish dishEntity);

    Page<DishVO> queryDish(Dish dish);

    Integer delById(Long[] ids);
    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    @Select("select status from dish where id = #{id}")
    Dish findById(Long id);
}
