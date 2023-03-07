package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.entity.ShoppingCart;
import com.example.reggie_take_out.mapper.ShoppingCartMapper;
import com.example.reggie_take_out.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/3/5-15:50
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
