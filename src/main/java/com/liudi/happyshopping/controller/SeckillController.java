package com.liudi.happyshopping.controller;

import com.liudi.happyshopping.access.AccessLimit;
import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.domain.OrderInfo;
import com.liudi.happyshopping.domain.SeckillOrder;
import com.liudi.happyshopping.rabbitmq.MQSender;
import com.liudi.happyshopping.rabbitmq.SeckillMessage;
import com.liudi.happyshopping.redis.GoodsKey;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.result.CodeMsg;
import com.liudi.happyshopping.result.Result;
import com.liudi.happyshopping.service.GoodsService;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import com.liudi.happyshopping.service.OrderService;
import com.liudi.happyshopping.service.SeckillService;
import com.liudi.happyshopping.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Autowired
    HappyShoppingUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    SeckillService seckillService;

    @Autowired
    MQSender sender;

    private Map<Long,Boolean> localOverMap = new HashMap<>();

    @RequestMapping("/do_seckill")
    @ResponseBody
    public String doSeckill(Model model, HappyShoppingUser user,
                            @RequestParam("goodsId")long goodsId){
        if (user==null)
            return "login";
        //determine stock
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock<=0){
            model.addAttribute("errmsg", CodeMsg.STOCK_EMPTY.getMsg());
            return "miaosha_fail";
        }
        //is successful
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goods.getId());
        if (order!=null){
            model.addAttribute("errmsg",CodeMsg.REPEAT_PURCHASE);
            return "miaosha_fail";
        }

        //1.decrease stock 2. make order 3. write order in a transaction
        OrderInfo orderInfo = seckillService.kill(user,goods);
        model.addAttribute("orderInfo",orderInfo);
        model.addAttribute("goods",goods);
        return "order_detail";
    }

    // get: every time result is equali : post:influence the data of server
    @RequestMapping(value = "/{path}/do_seckill2",method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doSeckill(HappyShoppingUser user,
                                     @RequestParam("goodsId")long goodsId,
                                     @PathVariable("path")String path){
        if (user==null)
            return Result.error(CodeMsg.SERVER_ERROR);
        boolean pathCheck = seckillService.checkService(user,goodsId,path);
        if (!pathCheck)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        Boolean isOver = localOverMap.get(goodsId);
        //if (!isOver)
        if (isOver)
            return Result.error(CodeMsg.STOCK_EMPTY);
        //decrease stock
        long stock = redisService.decrease(GoodsKey.getSeckillGoodsStock,""+goodsId);
        //if (stock< 0 ){
        if (stock< 0 ){
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.STOCK_EMPTY);
        }
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goodsId);
        if (order!=null)
            return Result.error(CodeMsg.REPEAT_PURCHASE);

        //send to queue
        SeckillMessage message = new SeckillMessage();
        message.setUser(user);
        message.setGoodsId(goodsId);
        sender.sendSeckillMessage(message);

        return Result.success(0);
//    //determine stock
//    GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
//    int stock = goods.getStockCount();
//    if (stock<=0){
//      return Result.error(CodeMsg.STOCK_EMPTY);
//    }
//    //is successful
//    SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goods.getId());
//    if (order!=null){
//      return Result.error(CodeMsg.REPEAT_PURCHASE);
//    }
//
//    //1.decrease stock 2. make order 3. write order in a transaction
//    OrderInfo orderInfo = seckillService.kill(user,goods);

//    return Result.success(orderInfo);
    }

    //after initialization
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsList= goodsService.listGoodsVo();
        if (goodsList==null )
            return;
        for (GoodsVo good:goodsList){
            redisService.set(GoodsKey.getSeckillGoodsStock,""+good.getId(),good.getGoodsStock());
            localOverMap.put(good.getId(),false);
        }
    }

    // 1:failed 0:still in queue orderId: success
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> getResult(HappyShoppingUser user,
                                  @RequestParam("goodsId")long goodsId){
        if (user == null)
            return Result.error(CodeMsg.MOBILE_NOT_EXIST);
        long result = seckillService.getSeckillResult(user.getId(),goodsId);
        return Result.success(result);
    }

    @AccessLimit(seconds = 5,maxCount=5,needLogin=true)
    @RequestMapping(value = "/path",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getPath(HttpServletRequest request,
                                  HappyShoppingUser user,
                                  @RequestParam("goodsId")long goodsId,
                                  @RequestParam("verifyCode")int verifyCode) {
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        //rate limiter
//    String url = request.getRequestURI();
//    String key = url+"_"+user.getId();
//    Integer count = redisService.get(AccessKey.access,key,Integer.class);
//    if (count == null){
//      redisService.set(AccessKey.access,key,1);
//    }
//    else if (count<5)
//      redisService.increase(AccessKey.access,key);
//    else
//      return Result.error(CodeMsg.ACCESS_LIMIT_MAX);
        boolean checkVerifyCode = seckillService.checkVerifyCode(user,goodsId,verifyCode);
        if (!checkVerifyCode)
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        String path = seckillService.creatSeckillPath(user,goodsId);
        return Result.success(path);
    }

    @RequestMapping(value = "verifyCode",method = RequestMethod.GET)
    @ResponseBody
    public Result<String> verifyCode(HttpServletResponse response, @RequestParam("goodsId") long goodsId, HappyShoppingUser user){
        if (user==null)
            return Result.error(CodeMsg.SESSION_ERROR);
        BufferedImage image = seckillService.createVerifyCode(goodsId,user);
        try{
            OutputStream out = response.getOutputStream();
            ImageIO.write(image,"JPEG",out);
            out.flush();
            out.close();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.error(CodeMsg.SERVER_ERROR);
    }
}