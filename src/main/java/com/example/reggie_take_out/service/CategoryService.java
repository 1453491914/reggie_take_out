package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.entity.Category;

/**
 * @author chengcheng
 * @version 1.0.0
 */

public interface CategoryService extends IService<Category> {

    public void deleteCategory(Long id);
}
