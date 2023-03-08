package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.dto.OrdersDto;
import com.example.reggie_take_out.entity.OrderDetail;
import com.example.reggie_take_out.entity.Orders;
import com.example.reggie_take_out.service.OrderDetailService;
import com.example.reggie_take_out.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author chengcheng
 * @version 1.0.0
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private HttpServletRequest request;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submitOrder(@RequestBody Orders orders) {
        ordersService.submitOrder(orders);
        return Result.success("下单成功！ ");

    }

    /**
     * 后台管理系统查看所有订单
     * @param page
     * @param pageSize
     * @param number
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page")
    public Result<Page> getOrderPage(int page, int pageSize, Long number, String beginTime,String endTime) {
        log.info("page : {} pageSize : {} number : {} beginTime : {} endTime : {}", page, pageSize, number,beginTime,endTime);

        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersLambdaQueryWrapper.like(number != null,Orders::getId,number);

        if (beginTime != null && endTime != null) {
            LocalDateTime selectBeginTime = LocalDateTime.parse(beginTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime selectEndTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            ordersLambdaQueryWrapper.between(Orders::getOrderTime,selectBeginTime,selectEndTime);
        }

        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersPage, ordersLambdaQueryWrapper);

        return Result.success(ordersPage);
    }

    /**
     * 用户移动端查看订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page> getUserOrders (int page, int pageSize) {
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();

        ordersLambdaQueryWrapper.eq(Orders::getUserId, request.getSession().getAttribute("user"));

        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);

        ordersService.page(ordersPage, ordersLambdaQueryWrapper);


        //获取订单明细
        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDtoPage, "records");

        List<Orders> ordersPageRecords = ordersPage.getRecords();


        List<OrdersDto> ordersDtoPageRecords= ordersPageRecords.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);

            Long itemId = item.getId();

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, itemId);
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);

            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoPageRecords);

        return Result.success(ordersDtoPage);
    }

    /**
     * 后台修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public Result<String> updateOrdersStatus (@RequestBody Orders orders) {
        log.info("status : {}  id : {}", orders.getStatus(), orders.getId());

        Orders ordersServiceById = ordersService.getById(orders.getId());

        ordersServiceById.setStatus(orders.getStatus());

        ordersService.updateById(ordersServiceById);

        return Result.success("订单状态修改成功！");
    }
}
