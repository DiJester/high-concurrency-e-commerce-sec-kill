package com.liudi.happyshopping.rabbitmq;

import com.liudi.happyshopping.domain.HappyShoppingUser;

public class SeckillMessage {
    private HappyShoppingUser user;
    private long goodsId;

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }

    public HappyShoppingUser getUser() {

        return user;
    }

    public void setUser(HappyShoppingUser user) {
        this.user = user;
    }
}
