package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavourMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
//由于需要对两张表进行插入，所以需要事务
@Transactional(rollbackFor = Exception.class)
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavourMapper dishFlavourMapper;
    @Override
    public void addDishWithFlavour(DishDTO dish) {
        Dish dishEntity = new Dish();
        List<DishFlavor> flavors = dish.getFlavors();
        BeanUtils.copyProperties(dish, dishEntity);
        //需要mybatis回传dishId:UserGeneratedKeys="true"和keyProperty="id"
        dishMapper.addDish(dishEntity);
        if(flavors != null && flavors.size() > 0) {
            flavors.forEach(flavor -> {flavor.setDishId(dishEntity.getId());});
            dishFlavourMapper.addFlavour(flavors);
        }
    }

    @Override
    public PageResult page(DishPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Dish dish = new Dish();
        BeanUtils.copyProperties(queryDTO, dish);
        Page<DishVO> page = dishMapper.queryDish(dish);
        long total = page.getTotal();
        List result = page.getResult();
        return new PageResult(total,result);
    }
}
