package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.SetMealDto;
import com.example.reggie_take_out.entity.SetMeal;

import java.util.List;

/**
 * @author chengcheng
 * @version 1.0.0
 */

public interface SetMealService extends IService<SetMeal> {

    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     * @param setMealDto
     */
    public void saveSetMealDish(SetMealDto setMealDto);

    /**
     * 删除套餐及套餐和菜品的关联关系
     * @param ids
     */
    public void removeSetMealDish(List<Long> ids);
}
