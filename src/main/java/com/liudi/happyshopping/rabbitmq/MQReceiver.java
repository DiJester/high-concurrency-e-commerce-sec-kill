package com.liudi.happyshopping.rabbitmq;

import com.liudi.happyshopping.domain.HappyShoppingUser;
import com.liudi.happyshopping.domain.OrderInfo;
import com.liudi.happyshopping.domain.SeckillOrder;
import com.liudi.happyshopping.redis.RedisService;
import com.liudi.happyshopping.service.GoodsService;
import com.liudi.happyshopping.service.HappyShoppingUserService;
import com.liudi.happyshopping.service.OrderService;
import com.liudi.happyshopping.service.SeckillService;
import com.liudi.happyshopping.vo.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {

    private static Logger log = LoggerFactory.getLogger(MQReceiver.class);

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


    @RabbitListener(queues = MQConfig.SECKILL_QUEUE)
    public void receive(String message){
        log.info("receive+"+message);
        SeckillMessage seckillMessage = RedisService.stringToBean(message,SeckillMessage.class);
        HappyShoppingUser user = seckillMessage.getUser();
        long goodsId = seckillMessage.getGoodsId();
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock<=0){
            return;
        }
        //is successful
        SeckillOrder order = orderService.getSeckillOrderByUserIdGoodsId(user.getId(),goods.getId());
        if (order!=null)
            return;
        OrderInfo orderInfo = seckillService.kill(user,goods);
    }
//  @RabbitListener(queues = MQConfig.QUEUE)
//  public void receice(String message){
//    log.info("Receive message"+message);
//  }
//
//  @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
//  public void receiveTopic1(String message){
//    log.info("topic q1"+message);
//  }
//
//  @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
//  public void receiveTopic2(String message){
//    log.info("topic q2"+message);
//  }
//
//  @RabbitListener(queues = MQConfig.HEADER_QUEUE)
//  public void receiveHeaderQueue(byte[] message){
//    log.info("header+"+message);
//  }
}