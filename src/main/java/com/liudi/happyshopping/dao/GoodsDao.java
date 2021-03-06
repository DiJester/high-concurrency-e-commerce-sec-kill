package com.liudi.happyshopping.dao;

import com.liudi.happyshopping.domain.SeckillGoods;
import com.liudi.happyshopping.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface GoodsDao {

    @Select("select g.*, sg.stock_count,sg.start_date,sg.end_date,sg.miaosha_price from seckill_goods sg left join goods g on sg.goods_id = g.id")
    List<GoodsVo> listGoodsVo();

    @Select("select g.*, sg.stock_count,sg.start_date,sg.end_date,sg.miaosha_price from seckill_goods sg left join goods g on sg.goods_id = g.id where g.id =#{goodsId}")
    GoodsVo getGoodsVoByGoodsId(@Param("goodsId") long goodsId);

    @Update("update seckill_goods set stock_count = stock_count-1 where goods_id = #{goodsId} and stock_count >0")
    void reduceStock(SeckillGoods good);
}