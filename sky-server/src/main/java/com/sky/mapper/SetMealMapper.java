package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetMealMapper {
    Page<SetmealVO> findAll(Setmeal setmeal);

    @AutoFill(value = OperationType.INSERT)
    void save(Setmeal setmeal);

    @Select("select * from setmeal where id = #{id}")
    SetmealVO findById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    void delSetmealByIds(Long[] ids);

    @Select("select * from setmeal where category_id = #{categoryId}")
    List<Setmeal> findByCategoryId(Integer categoryId);

    Integer countStatus(int status);
}
