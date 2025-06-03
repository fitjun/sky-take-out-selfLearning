package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Override
    public PageResult page(SetmealPageQueryDTO queryDTO) {
        PageHelper.startPage(queryDTO.getPage(),queryDTO.getPageSize());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(queryDTO,setmeal);
        Page<SetmealVO> page = setMealMapper.findAll(setmeal);
        long total = page.getTotal();
        List<SetmealVO> result = page.getResult();
        return new PageResult(total, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.save(setmeal);
        //插入套餐后获取返回的id，加入套餐对应的菜品。还要在套餐、菜品表中插入两个菜品是套餐关系
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmeal.getId());});
        setMealDishMapper.addMealDish(setmealDishes);
    }

    @Override
    public SetmealVO findSetMealById(Long id) {
        SetmealVO setmealVO = setMealMapper.findById(id);
        List<SetmealDish> setmealDishes = setMealDishMapper.findBySetMealId(id);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSetMeal(SetmealDTO setmealDTO) {
        //更新setmeal表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setMealMapper.update(setmeal);
        //删除setmeal_dish表对应setmeal_id原来的数据，并重新插入，注意重新将setmeal_id赋值，因为新加入的没有setmeal_id
        Long[]ids = {setmeal.getId()};//传数组方便后面批量删除可以复用
        setMealDishMapper.delBySetmealIds(ids);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.forEach(setmealDish -> {setmealDish.setSetmealId(setmeal.getId());});
        setMealDishMapper.addMealDish(setmealDishes);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Integer status,Long id) {
        Setmeal setmeal = new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setMealMapper.update(setmeal);
        //套餐起售对应的菜品也应该起售反之也是 因为是操作dish表，所以应该用dishMapper
        dishMapper.updateDishBySetmealId(id,status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delSetmealById(Long[] ids) {
        //起售无法删除
        for (Long id : ids) {
            SetmealVO setMeal = findSetMealById(id);
            if(setMeal.getStatus()!=0){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
        setMealMapper.delSetmealByIds(ids);
        //同时删除setmealdish表中对应套餐数据
        setMealDishMapper.delBySetmealIds(ids);
    }

    @Override
    public List<Setmeal> findSetmealByCategoryId(Integer categoryId) {
        List<Setmeal> setmeals = setMealMapper.findByCategoryId(categoryId);
        return setmeals;
    }

    @Override
    public List<Dish> findDishBySetmealId(Integer semealId) {
        List<Dish> dishes =dishMapper.findBySetmealId(semealId);
        return dishes;
    }
}
