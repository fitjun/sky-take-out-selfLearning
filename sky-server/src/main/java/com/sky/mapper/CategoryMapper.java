package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    Page<Category> findCategory(CategoryPageQueryDTO categoryPageQueryDTO);

    void update(Category categoryEntity);
    @Insert("INSERT INTO category ( type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "VALUE (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void addCatagory(Category category);

    List<Category> listByType(Integer type);
}

