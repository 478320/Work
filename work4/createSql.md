CREATE TABLE `tb_blog` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '文章唯一id',
  `user_id` bigint NOT NULL COMMENT '创建文章的用户id',
  `title` varchar(255) NOT NULL COMMENT '文章标题',
  `content` text NOT NULL COMMENT '文章内容',
  `liked` int DEFAULT '0' COMMENT '点赞数',
  `views` int DEFAULT '0' COMMENT '点击量',
  `create_time` datetime NOT NULL COMMENT '创建文章时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `tb_blog_comments` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint unsigned NOT NULL COMMENT '用户id',
  `blog_id` bigint unsigned NOT NULL COMMENT '文章id',
  `parent_id` bigint unsigned NOT NULL DEFAULT '0' COMMENT '关联的1级评论id，如果是一级评论，则值为0',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '回复的内容',
  `liked` int unsigned DEFAULT '0' COMMENT '点赞数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci ROW_FORMAT=COMPACT;

CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户唯一id',
  `username` varchar(255) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '用户密码',
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '用户头像信息',
  `motto` varchar(255) NOT NULL DEFAULT '每天都有好心情，写点什么吧' COMMENT '用户签名',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_username` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1759471999859605506 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;