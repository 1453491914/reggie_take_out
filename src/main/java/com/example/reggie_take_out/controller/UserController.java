package com.example.reggie_take_out.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.example.reggie_take_out.common.Result;
import com.example.reggie_take_out.entity.User;
import com.example.reggie_take_out.service.UserService;
import com.example.reggie_take_out.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author chengcheng
 * @version 1.0.0
 * @createTime 2023/3/4-11:07
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 发送短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user , HttpSession session){
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            String code  = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code:{}", code);

            //调用工具类，发送短信验证码
            //SMSUtils.sendMessage();

            //将生成的验证码保存在session
            session.setAttribute(phone,code);

            return Result.success("手机验证吗发送成功");

        }

        return Result.error("短信发送失败");
    }

    /**
     * 移动端用户登陆
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map user, HttpSession session){
        log.info("user : {}", user);

        //获取手机号
        String phone = user.get("phone").toString();

        //获取验证码
        String code = user.get("code").toString();

        //从session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对
        if (codeInSession != null && codeInSession.equals(code)) {
            //比对成功则登陆成功

            //用该手机号与数据库中比较，如果为新用户，则自动完成注册
            LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
            userLambdaQueryWrapper.eq(User::getPhone, phone);

            User registerUser = userService.getOne(userLambdaQueryWrapper);
            if ( registerUser == null) {
                //创建新用户
                registerUser = new User();
                registerUser.setPhone(phone);

                //将新用户储存在数据库
                userService.save(registerUser);
            }

            session.setAttribute("user",registerUser.getId());
            return Result.success(registerUser);
        }

        return Result.error("登陆失败");
    }





}
