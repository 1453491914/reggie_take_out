package com.example.reggie_take_out.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.reggie_take_out.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
