package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.mapper.UserMapper;
import com.example.reggie_take_out.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/3/4-11:02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
