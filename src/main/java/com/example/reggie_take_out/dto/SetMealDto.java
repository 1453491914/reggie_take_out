package com.example.reggie_take_out.dto;

import com.example.reggie_take_out.entity.SetMeal;
import com.example.reggie_take_out.entity.SetMealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetMealDto extends SetMeal {

    private List<SetMealDish> setmealDishes;

    private String categoryName;
}
