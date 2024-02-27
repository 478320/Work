package com.huayu.utils;

/**
 * Redis数据库使用的常量
 */
public class RedisConstans {

    public static final String LOGIN_USER_KEY = "login:";
    public static final Long LOGIN_USER_TTL = 34200L;

    public static final String LIKE_BLOG_KEY = "blog:liked:";
    public static final String LIKE_BLOG_COMMENTS_KEY = "blog:comments:liked:";

    public static final String NEW_BLOG_KEY = "blog:new";

}
