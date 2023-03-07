package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.common.CustomException;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.SetMeal;
import com.example.reggie_take_out.mapper.CategoryMapper;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishService;
import com.example.reggie_take_out.service.SetMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/2/26-09:40
 */

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;
    
    @Autowired
    private SetMealService setMealService;
    
    @Override
    public void deleteCategory(Long id) {

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据传入id查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long dishCount = dishService.count(dishLambdaQueryWrapper);

        //查询当前分类是否关联了菜品，如果有，则删除失败
        if (dishCount > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果有，则删除失败
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据传入id查询
        setMealLambdaQueryWrapper.eq(SetMeal::getCategoryId, id);
        long setMealCount = setMealService.count(setMealLambdaQueryWrapper);
        if (setMealCount > 0) {
            throw new RuntimeException("当前分类关联了套餐，不能删除");
        }

        //若都没有，则执行删除操作
        this.removeById(id);
    }
}
