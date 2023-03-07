package com.example.reggie_take_out.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.reggie_take_out.entity.AddressBook;
import com.example.reggie_take_out.mapper.AddressBookMapper;
import com.example.reggie_take_out.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/3/5-09:37
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
