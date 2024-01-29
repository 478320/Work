package com.huayu.utils;

/**
 * Redis数据库使用的常量
 */
public class RedisConstans {

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 34200L;
    public static final Long CACHE_NULL_TTL = 2L;
    public static final Long CACHE_TASK_TTL = 30L;
    public static final String CACHE_TASK_KEY = "cache:task:";

}
