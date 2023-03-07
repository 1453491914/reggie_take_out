package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.entity.OrderDetail;
import com.example.reggie_take_out.mapper.OrderDetailMapper;
import com.example.reggie_take_out.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/3/6-11:34
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
