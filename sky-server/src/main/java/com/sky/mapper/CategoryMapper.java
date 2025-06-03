package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    Page<Category> findCategory(Category category);

    @AutoFill(OperationType.UPDATE)
    void update(Category categoryEntity);

    @AutoFill(value = OperationType.INSERT)
    @Insert("INSERT INTO category ( type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUE (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addCatagory(Category category);


    @Delete("DELETE FROM category WHERE id = #{id}")
    void delCatagoryById(Long id);
}

