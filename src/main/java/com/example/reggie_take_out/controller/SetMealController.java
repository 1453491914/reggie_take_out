package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.dto.SetMealDto;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.entity.SetMeal;
import com.example.reggie_take_out.service.CategoryService;
import com.example.reggie_take_out.service.SetMealDishService;
import com.example.reggie_take_out.service.SetMealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetMealController {

    @Autowired
    private SetMealService setMealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetMealDishService setMealDishService;

    /**
     * 新增套餐
     *
     * @param setMealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setMealCache", allEntries = true)
    public Result<String> saveSetMeal(@RequestBody SetMealDto setMealDto) {

        log.info("套餐信息：{}", setMealDto);

        setMealService.saveSetMealDish(setMealDto);
        return Result.success("新增套餐成功");
    }


    /**
     * 套餐的分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> getSetMealPage(int page, int pageSize, String name) {
        log.info("page : {} pageSize : {} name : {}", page, pageSize, name);

        Page<SetMeal> setMealPage = new Page<>(page, pageSize);


        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();

        setMealLambdaQueryWrapper.like(name != null, SetMeal::getName, name);

        setMealLambdaQueryWrapper.orderByDesc(SetMeal::getUpdateTime);

        setMealService.page(setMealPage, setMealLambdaQueryWrapper);

        //获取套餐分类
        Page<SetMealDto> setMealDtoPage = new Page<>();

        BeanUtils.copyProperties(setMealPage, setMealDtoPage, "records");

        List<SetMeal> setMealPageRecords = setMealPage.getRecords();

        List<SetMealDto> setMealDtoRecords = setMealPageRecords.stream().map((item) -> {
            SetMealDto setMealDto = new SetMealDto();
            BeanUtils.copyProperties(item, setMealDto);
            //获取List中每个元素的分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setMealDto.setCategoryName(category.getName());
            }
            return setMealDto;
        }).collect(Collectors.toList());

        setMealDtoPage.setRecords(setMealDtoRecords);

        return Result.success(setMealDtoPage);
    }

    /**
     * 删除套餐
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setMealCache", allEntries = true ) //清除缓存
    public Result<String> deleteSetMeal(@RequestParam List<Long> ids) {
        log.info("ids : {}", ids);
        setMealService.removeSetMealDish(ids);
        return Result.success("套餐删除成功");
    }

    /**
     * 查询套餐数据
     *
     * @param setMeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setMealCache", key = " #setMeal.categoryId + '_' + #setMeal.status")
    public Result<List<SetMeal>> getSetMealList(SetMeal setMeal) {
        log.info("setMeal : {}", setMeal);
        LambdaQueryWrapper<SetMeal> setMealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setMealLambdaQueryWrapper.eq(setMeal.getCategoryId() != null, SetMeal::getCategoryId, setMeal.getCategoryId());
        setMealLambdaQueryWrapper.eq(setMeal.getStatus() != null, SetMeal::getStatus, setMeal.getStatus());
        setMealLambdaQueryWrapper.orderByDesc(SetMeal::getUpdateTime);

        List<SetMeal> setMealList = setMealService.list(setMealLambdaQueryWrapper);

        return Result.success(setMealList);
    }

    /**
     * 停售套餐
     * @param ids
     * @return
     */
    @PostMapping("/status/0")
    public Result<String> stopSetMeal(Long ids) {
        log.info("ids : {}", ids);
        SetMeal setMealById = setMealService.getById(ids);

        setMealById.setStatus(0);

        setMealService.updateById(setMealById);

        return Result.success("修改成功！");
    }

    /**
     * 启售套餐
     * @param ids
     * @return
     */
    @PostMapping("/status/1")
    public Result<String> startSetMealStatus(Long ids) {
        log.info("ids : {}", ids);
        SetMeal setMealById = setMealService.getById(ids);

        setMealById.setStatus(1);

        setMealService.updateById(setMealById);

        return Result.success("修改成功！");
    }

    /**
     * 修改套餐
     *
     * @param setMealDto
     * @return
     */
    @PutMapping
    public Result<String> updateDish(@RequestBody SetMealDto setMealDto) {

        setMealService.updateSetMealDish(setMealDto);

        return Result.success("修改套餐成功");
    }

    /**
     * 根据id查询套餐信息和套餐和菜品的关联信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetMealDto> getDishById(@PathVariable Long id) {
        SetMealDto setMealDishById = setMealService.getSetMealDishById(id);
        return Result.success(setMealDishById);
    }
}

