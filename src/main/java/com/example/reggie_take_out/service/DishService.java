package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Dish;

/**
 * @author chengcheng
 * @version 1.0.0
 */
public interface DishService extends IService<Dish> {
    //添加菜品以及对应的口味
    public void addDishWithFlavor(DishDto dishDto);

    //获取菜品及对应的口味
    public DishDto getDishWithFlavorById (Long id);

    //更新菜品及对应的口味
    public void updateDishWithFlavor(DishDto dishDto);
}
