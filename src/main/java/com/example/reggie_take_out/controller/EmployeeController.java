package com.example.reggie_take_out.controller;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.BaseContext;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.entity.Employee;
import com.example.reggie_take_out.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author chengcheng
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     * @param request
     * @param employee 将登陆提交的用户名密码从json转成对象
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {

        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return Result.error("用户名不存在");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return Result.error("密码错误");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return Result.error("该员工已被禁用");
        }

        //6、登录成功，將员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        log.info("用戸登陆成功，用戸id: {}", request.getSession().getAttribute("employee"));
        Long employeeId = (Long) request.getSession().getAttribute("employee");
        BaseContext.setCurrentId(employeeId);
        log.info("BaseContextId: {}", BaseContext.getCurrentId());
        log.info("threadLocalId: {}", Thread.currentThread().getId());
        return Result.success(emp);
    }

    /**
     * 员工登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request ){
        //1、清理Session中的员工信息
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    @PostMapping
    public Result<String> saveEmployee(HttpServletRequest request,@RequestBody Employee employee) {
        log.info("保存员工信息：{}", employee.toString());

        //给员工设置初始密码，并用md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Result.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> pageResult(int page, int pageSize,String name) {
        //log.info("员工分页查询，当前页：{}，每页：{}, 名称：{}", page,pageSize,name);

        //构建分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);

        //构建查询条件
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);

        //添加排序条件
        employeeLambdaQueryWrapper.orderByDesc(Employee::getCreateTime);

        //执行查询
        employeeService.page(pageInfo,employeeLambdaQueryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * 更新员工状态
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public Result<String> updateStatus(HttpServletRequest request,@RequestBody Employee employee) {
        Long empId =(Long)request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return Result.success("更新员工状态成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return Result.success(employee);
        }
        return Result.error("员工不存在");
    }
}
