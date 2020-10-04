package com.liudi.happyshopping.redis;

public interface KeyPrefix {

    int expireSeconds();

    String getPrefix();

}
