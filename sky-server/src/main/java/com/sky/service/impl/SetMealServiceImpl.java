package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;
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
}
