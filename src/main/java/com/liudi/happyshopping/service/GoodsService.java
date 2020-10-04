package com.liudi.happyshopping.service;

import com.liudi.happyshopping.dao.GoodsDao;
import com.liudi.happyshopping.domain.SeckillGoods;
import com.liudi.happyshopping.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo(){
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return goodsDao.getGoodsVoByGoodsId(goodsId);
    }

    public boolean reduceStock(GoodsVo goods) {
        SeckillGoods good = new SeckillGoods();
        good.setGoodsId(goods.getId());
        good.setStockCount(goods.getStockCount()-1);
        goodsDao.reduceStock(good);
        return true;
    }
}