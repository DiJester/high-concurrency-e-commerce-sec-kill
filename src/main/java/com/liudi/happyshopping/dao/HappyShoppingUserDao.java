package com.liudi.happyshopping.dao;

import com.liudi.happyshopping.domain.HappyShoppingUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface HappyShoppingUserDao {

    @Select("select * from happyshopping_user where id = #{id}")
    HappyShoppingUser getById(@Param("id") long id);

    @Update("update happyshopping_user set password = #{pawword} where id = #{id}")
    void update(HappyShoppingUser user);

    @Insert("insert into happyshopping_user(id,nick_name,password,salt,register_date,gender)" +
            "values(#{id},#{nickname},#{password},#{salt},now(),#{gender}) ")
    int insert(HappyShoppingUser user);
}
