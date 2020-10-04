package com.liudi.happyshopping.controller;

import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.rabbitmq.MQSender;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.result.Result;
import com.liudi.happyshopping.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sample")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/info")
    @ResponseBody
    public Result<HappyShoppingUser> info(HappyShoppingUser user){
        return Result.success(user);
    }

//  @RequestMapping("/mq")
//  @ResponseBody
//  public Result<String> mq(){
//    sender.sendSeckillMessage("hello mq");
//    return Result.success("send ok");
//  }
}