package com.example.reggie_take_out.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author chengcheng
 * @version 1.0.0
 */

/**
 * 自定义的元数据处理器
 */
@Component
@Slf4j
public class ReggieMetaObjectHandler implements MetaObjectHandler {
    @Autowired
    private HttpServletRequest request;

    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        Object employee = request.getSession().getAttribute("employee");
        Object user = request.getSession().getAttribute("user");

        if (employee != null) {
            metaObject.setValue("createUser", employee);
            metaObject.setValue("updateUser", employee);
        } else if (user != null) {
            metaObject.setValue("createUser", user);
            metaObject.setValue("updateUser", user);
        }


    }

    /**
     * 更新操作自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充【update】");
        metaObject.setValue("updateTime", LocalDateTime.now());
        Object employee = request.getSession().getAttribute("employee");
        Object user = request.getSession().getAttribute("user");

        if (employee != null) {
            metaObject.setValue("updateUser", employee);
        } else if (user != null) {
            metaObject.setValue("updateUser", user);
        }

    }
}
