package com.liudi.happyshopping.controller;

import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.redis.GoodsKey;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.result.Result;
import com.liudi.happyshopping.service.GoodsService;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import com.liudi.happyshopping.vo.GoodsDetailVo;
import com.liudi.happyshopping.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    HappyShoppingUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;

    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String toGoodsList(HttpServletRequest request,
                              HttpServletResponse response,
                              Model model, HappyShoppingUser user){
        System.out.println("user is :"+user);
        String html = redisService.get(GoodsKey.getGoodsList,"",String.class);
        if (html!=null)
            return html;
        List<GoodsVo> goods = goodsService.listGoodsVo();
        model.addAttribute("goodsList",goods);
//    return "goods_list";
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list",context);
        if (html!=null){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }

    @RequestMapping(value = "/to_detail/{goodsId}",produces = "text/html")
    @ResponseBody
    public String toDetail(HttpServletRequest request,
                           HttpServletResponse response,
                           Model model, HappyShoppingUser user,
                           @PathVariable("goodsId")long goodsId){
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        String html = redisService.get(GoodsKey.getGoodsDetail,String.valueOf(goodsId),String.class);
        if (html!=null)
            return html;
        model.addAttribute("user",user);
        model.addAttribute("goods",goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus;
        int remainSeconds = 0;
        if (now < startAt){
            seckillStatus = 0;
            remainSeconds = (int)(startAt-now)/1000;
        }
        else if (now > endAt){
            seckillStatus=2;
        }
        else{
            seckillStatus = 1;
        }
        model.addAttribute("seckillStatus",seckillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        WebContext context = new WebContext(request,response,request.getServletContext(),request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail",context);
        redisService.set(GoodsKey.getGoodsDetail,String.valueOf(goodsId),html);
        return html;
    }

    @RequestMapping(value = "/to_detail2/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> toDetail2(HttpServletRequest request,
                                           HttpServletResponse response,
                                           Model model, HappyShoppingUser user,
                                           @PathVariable("goodsId")long goodsId){
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        System.out.println("goods:"+goods);
        long startAt = goods.getStartDate().getTime();
        long endAt = goods.getEndDate().getTime();
        long now = System.currentTimeMillis();
        int seckillStatus;
        int remainSeconds = 0;
        if (now < startAt){
            seckillStatus = 0;
            remainSeconds = (int)(startAt-now)/1000;
        }
        else if (now > endAt){
            seckillStatus=2;
            remainSeconds = -1;
        }
        else{
            seckillStatus = 1;
            remainSeconds = (int)(endAt - now)/1000;
        }

        GoodsDetailVo goodsDetailVo = new GoodsDetailVo();
        goodsDetailVo.setGoods(goods);
        goodsDetailVo.setRemainSeconds(remainSeconds);
        goodsDetailVo.setSeckillStatus(seckillStatus);
        goodsDetailVo.setUser(user);
        return Result.success(goodsDetailVo);
    }
}