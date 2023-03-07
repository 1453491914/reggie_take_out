package com.example.reggie_take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie_take_out.entity.Employee;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@Transactional
public interface EmployeeService extends IService<Employee> {
}
