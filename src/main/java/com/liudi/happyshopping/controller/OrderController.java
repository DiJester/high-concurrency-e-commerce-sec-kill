package com.liudi.happyshopping.controller;


import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.domain.OrderInfo;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.result.CodeMsg;
import com.liudi.happyshopping.result.Result;
import com.liudi.happyshopping.service.GoodsService;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import com.liudi.happyshopping.service.OrderService;
import com.liudi.happyshopping.vo.GoodsVo;
import com.liudi.happyshopping.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    HappyShoppingUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> info(HappyShoppingUser user,
                                      @RequestParam("orderId") long orderId) {
        if(user == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        OrderInfo order = orderService.getOrderById(orderId);
        if(order == null) {
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        long goodsId = order.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo vo = new OrderDetailVo();
        vo.setOrder(order);
        vo.setGoods(goods);
        return Result.success(vo);
    }

}
