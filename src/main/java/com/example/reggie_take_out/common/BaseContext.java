package com.example.reggie_take_out.common;

/**
 * @author chengcheng
 * @version 1.0.0
 */

/**
 * 基于ThreadLocal封裝工具类，用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static Long getCurrentId() {
        return threadLocal.get();
    }

    public static void setCurrentId(Long employeeId) {
        threadLocal.set(employeeId);

    }
}
