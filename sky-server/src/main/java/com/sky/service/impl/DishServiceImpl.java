package com.sky.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavourMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
//由于需要对两张表进行插入，所以需要事务
@Transactional(rollbackFor = Exception.class)
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavourMapper dishFlavourMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private RedisTemplate redisTemplate;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delDish(Long[] ids) {
        //查询每个主键的status，如果有正在起售就全部不能删除
        for (Long id : ids) {
            Dish dish = dishMapper.findById(id);
            Long setmealId = setMealDishMapper.findByDishId(id);
            if(dish.getStatus()!= StatusConstant.DISABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            if(setmealId != null){
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        dishMapper.delById(ids);
        dishFlavourMapper.delByDishId(ids);
    }

    @Override
    public void ChangeStatus(Integer status, Long id) {
        Dish dish = new Dish();
        dish.setId(id);
        dish.setStatus(status);
        dishMapper.updateDish(dish);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DishVO findDishAndFlavorById(Long id) {
        Dish dish = dishMapper.findById(id);
        List<DishFlavor> dishFlavors = dishFlavourMapper.findByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        //删了重加，省的麻烦
        Long [] ids = {dish.getId()};
        dishFlavourMapper.delByDishId(ids);
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        //新加入的口味没有dishid，要手动加上，为空则不做任何事情
        if(dishFlavors != null && dishFlavors.size() > 0) {
            dishFlavors.forEach(flavor -> {flavor.setDishId(dish.getId());});
            dishFlavourMapper.addFlavour(dishFlavors);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<DishVO> findBycatagoryId(Long id) {
        String key = "DISH:"+ id;
        //redis判断，redis中没有再访问数据库
        String json = (String) redisTemplate.opsForValue().get(key);
        List<DishVO> dishes = JSONUtil.toList(json, DishVO.class);
        if (dishes != null && dishes.size() > 0) {
            log.info("redis查询成功：{}", dishes);
            return dishes;
        }
        //redis中没有数据，查询数据库并插入redis
        dishes = dishMapper.findByCatagoryId(id);
        dishes.forEach(dish -> {
            dish.setFlavors(findDishAndFlavorById(dish.getId()).getFlavors());
            dish.setCategoryId(id);
        });
        String jsonString = JSONUtil.toJsonStr(dishes);
        redisTemplate.opsForValue().set(key,jsonString);
        return dishes;
    }
}
