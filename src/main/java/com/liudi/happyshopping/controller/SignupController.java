package com.liudi.happyshopping.controller;

import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.result.Result;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import com.liudi.happyshopping.service.UserService;
import com.liudi.happyshopping.vo.LoginVo;
import com.liudi.happyshopping.vo.SignupVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/signup")
public class SignupController {
    private static Logger log = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    HappyShoppingUserService happyShoppingUserService;

    @RequestMapping("/to_signup")
    public String toSignup(){
        return "signup";
    }

    @RequestMapping("/do_signup")
    @ResponseBody
    public Result<Boolean> doSignup(HttpServletResponse response, @Valid SignupVo signupVo){
        System.out.println("doSignup");
        log.info("doSignup:"+signupVo.toString());
        //login
        happyShoppingUserService.signup(response,signupVo);
        return Result.success(true);
    }

}
