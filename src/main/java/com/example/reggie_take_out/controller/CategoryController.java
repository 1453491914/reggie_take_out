package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.entity.Category;
import com.example.reggie_take_out.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public Result<String> addCategory(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success("新增分类成功");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageResult(int page,int pageSize){

        //构建分页构造器
        Page<Category> pageInfo = new Page(page, pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加排序条件，根据sort字段排序
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort);

        //执行查询
        categoryService.page(pageInfo,categoryLambdaQueryWrapper);

        return Result.success(pageInfo);

    }

    /**
     * 删除菜品分类
     * @param ids
     * @return
     */
    @DeleteMapping 
    public Result<String> deleteCategory(Long ids) {

        categoryService.deleteCategory(ids);
        return Result.success("删除分类成功");
    }

    /**
     * 修改菜品分类
     * @param category
     * @return
     */
    @PutMapping
    public Result<String> updateCategory(@RequestBody Category category) {
        log.info("修改分类信息:{}",category);
        categoryService.updateById(category);
        return Result.success("修改分类成功");
    }

    /**
     * 获得所有菜品分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> getList(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(categoryLambdaQueryWrapper);
        return Result.success(categoryList);
    }
}
