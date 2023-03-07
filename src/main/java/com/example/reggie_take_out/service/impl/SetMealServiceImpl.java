package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.dto.SetMealDto;
import com.example.reggie_take_out.entity.SetMeal;
import com.example.reggie_take_out.entity.SetMealDish;
import com.example.reggie_take_out.mapper.SetMealMapper;
import com.example.reggie_take_out.service.SetMealDishService;
import com.example.reggie_take_out.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/2/26-18:43
 */
@Service
@Slf4j
public class SetMealServiceImpl extends ServiceImpl<SetMealMapper, SetMeal> implements SetMealService {


    @Autowired
    private SetMealDishService setMealDishService;


    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     *
     * @param setMealDto
     */
    @Transactional
    @Override
    public void saveSetMealDish(SetMealDto setMealDto) {
        //保存套餐的基本信息，操作set_meal表，执行insert操作
        this.save(setMealDto);

        List<SetMealDish> setmealDishes = setMealDto.getSetmealDishes();

        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setMealDto.getId());
            return item;
        }).collect(Collectors.toList());

        //保存菜品和套餐的关联信息，操作set_meal_dish表，执行insert操作
        setMealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐及套餐和菜品的关联关系
     *
     * @param ids
     */
    @Transactional
    @Override
    public void removeSetMealDish(List<Long> ids) {
        //查询套餐状态，判断是否为停售状态
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.in(SetMeal::getId, ids);
        setMealLambdaQueryWrapper.eq(SetMeal::getStatus, 1);

        //若为在售状态，抛出业务异常
        long count = this.count(setMealLambdaQueryWrapper);

        if (count > 0) {
            throw new CustomException("含有正在售卖的套餐，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

        //再删除关系表中的数据
        LambdaQueryWrapper<SetMealDish> setMealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealDishLambdaQueryWrapper.in(SetMealDish::getSetmealId, ids);

        setMealDishService.remove(setMealDishLambdaQueryWrapper);

    }
}
