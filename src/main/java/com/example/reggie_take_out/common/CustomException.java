package com.example.reggie_take_out.common;

/**
 * @author chengcheng
 * @version 1.0.0
 */

/**
 * 自定义业务异常
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
