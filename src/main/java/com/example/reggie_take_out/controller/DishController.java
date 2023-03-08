package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.dto.DishDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.Dish;
import com.example.reggie_take_out.entity.DishFlavor;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.DishFlavorService;
import com.example.reggie_take_out.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> saveDish(@RequestBody DishDto dishDto) {

        dishService.addDishWithFlavor(dishDto);

        return Result.success("新增菜品成功");
    }

    /**
     * 菜品的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageResult(int page, int pageSize, String name) {
        //构造分页构造器
        Page<Dish> dishPage = new Page<Dish>(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        dishLambdaQueryWrapper.like(name != null, Dish::getName, name);
        dishLambdaQueryWrapper.eq(Dish::getIsDeleted, 0);

        //添加排序条件
        dishLambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(dishPage, dishLambdaQueryWrapper);

        //将dishPage中的数据拷贝到dishDtoPage中
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> dishPageRecords = dishPage.getRecords();
        List<DishDto> dtoList = dishPageRecords.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //获取分类Id

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dtoList);

        return Result.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishById(@PathVariable Long id) {
        DishDto dishWithFlavor = dishService.getDishWithFlavorById(id);
        return Result.success(dishWithFlavor);
    }


    /**
     * 修改菜品
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> updateDish(@RequestBody DishDto dishDto) {

        dishService.updateDishWithFlavor(dishDto);

        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);  


        return Result.success("修改菜品成功");
    }

    /**
     * 停售菜品
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public Result<String> setStopStatus(Long ids) {
        log.info("ids = {}", ids);
        Dish dish = dishService.getById(ids);
        if (dish.getStatus() == 1) {
            dish.setStatus(0);
        }
        dishService.updateById(dish);

        return Result.success("停售菜品成功");

    }

    /**
     * 起售菜品
     *
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public Result<String> setStartStatus(Long ids) {
        log.info("ids = {}", ids);
        Dish dish = dishService.getById(ids);
        if (dish.getStatus() == 0) {
            dish.setStatus(1);
        }
        dishService.updateById(dish);

        return Result.success("起售菜品成功");
    }

    /**
     * 删除菜品
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> deleteDish(Long ids) {
        Dish dish = dishService.getById(ids);
        dish.setIsDeleted(1);
        dishService.updateById(dish);

        return Result.success("菜品删除成功");
    }

    /**
     * 根据分类的id来查询分类下的菜品信息
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> getDishByCategory(Dish dish) {
        //首先从redis中获取缓存数据
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();

        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果redis中有数据则直接返回无需查询数据库

        if (dishDtoList != null) {
            return Result.success(dishDtoList);
        }

        
        //如果不存在则查询数据库，将查询到的数据缓存在redis

        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        dishLambdaQueryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getIsDeleted, 0);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);

        dishLambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishList = dishService.list(dishLambdaQueryWrapper);

        dishDtoList = dishList.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId(); //获取分类Id

            Category category = categoryService.getById(categoryId);

            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());


        redisTemplate.opsForValue().set(key, dishDtoList,60, TimeUnit.MINUTES);

        return Result.success(dishDtoList);
    }

}
