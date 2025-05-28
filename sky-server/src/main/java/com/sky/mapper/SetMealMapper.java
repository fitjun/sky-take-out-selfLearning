package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SetMealMapper {
    Page<SetmealVO> findAll(Setmeal setmeal);

    @AutoFill(value = OperationType.INSERT)
    void save(Setmeal setmeal);
}
