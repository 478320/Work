package com.huayu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huayu.dto.BlogCommentDTO;
import com.huayu.domain.BlogComments;
import com.huayu.domain.User;
import com.huayu.dto.LoginUser;
import com.huayu.dto.Result;
import com.huayu.exception.BusinessException;
import com.huayu.mapper.BlogCommentsMapper;
import com.huayu.mapper.UserMapper;
import com.huayu.service.IBlogCommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huayu.utils.Code;
import com.huayu.utils.RedisConstans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.huayu.utils.Code.SUCCESS;

/**
 * blogComments服务层实现类
 */
@Service
public class BlogCommentsServiceImpl extends ServiceImpl<BlogCommentsMapper, BlogComments> implements IBlogCommentsService {

    @Autowired
    private BlogCommentsMapper blogCommentsMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getBlogComments(Long id) {
        LambdaQueryWrapper<BlogComments> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BlogComments::getBlogId, id);
        queryWrapper.eq(BlogComments::getParentId, 0);
        queryWrapper.orderByDesc(BlogComments::getCreateTime);
        List<BlogComments> blogComments = blogCommentsMapper.selectList(queryWrapper);
        if (blogComments.isEmpty() || blogComments == null) {
            return new Result(SUCCESS, "", Collections.emptyList());
        }
        //为查询出的父评论赋值
        List<BlogComments> collect = blogComments.stream().map(blogComments1 -> {
            Long userBlogId = blogComments1.getUserId();
            User user = userMapper.selectById(userBlogId);
            blogComments1.setAvatarUrl(user.getAvatarUrl());
            blogComments1.setUsername(user.getUsername());
            return blogComments1;
        }).collect(Collectors.toList());
        //未登录情况下，利用父评论中的id查询子评论，为子评论赋值后，放到返回对象中
        List<BlogCommentDTO> collectDTO = collect.stream().map(blogComments1 -> BeanUtil.copyProperties(blogComments1, BlogCommentDTO.class)).collect(Collectors.toList());
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser"){
            List<BlogCommentDTO> collect1 = collectDTO.stream().map(blogCommentDTO -> {
                blogCommentDTO.setIsLike(false);
                LambdaQueryWrapper<BlogComments> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BlogComments::getParentId, blogCommentDTO.getId());
                queryWrapper1.orderByDesc(BlogComments::getCreateTime);
                List<BlogComments> blogComments1 = blogCommentsMapper.selectList(queryWrapper1);
                if (!blogComments1.isEmpty()) {
                    List<BlogComments> collect2 = blogComments1.stream().map(blogComments2 -> {
                        Long userBlogId = blogComments2.getUserId();
                        User user = userMapper.selectById(userBlogId);
                        blogComments2.setAvatarUrl(user.getAvatarUrl());
                        blogComments2.setUsername(user.getUsername());
                        return blogComments2;
                    }).collect(Collectors.toList());
                    blogCommentDTO.setChildren(collect2);
                } else {
                    blogCommentDTO.setChildren(Collections.emptyList());
                }
                return blogCommentDTO;
            }).collect(Collectors.toList());
            return new Result(SUCCESS, "", collect1);
        }else {
            //登录情况下，利用父评论中的id查询子评论，为子评论赋值后，放到返回对象中
            LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principal.getUser().getId();
            String key = RedisConstans.LIKE_BLOG_COMMENTS_KEY + userId;
            //判断登录用户是否点赞
            List<BlogCommentDTO> collect1 = collectDTO.stream().map(blogCommentDTO -> {
                Double score = stringRedisTemplate.opsForZSet().score(key, blogCommentDTO.getId().toString());
                if (score == null){
                    blogCommentDTO.setIsLike(false);
                }else {
                    blogCommentDTO.setIsLike(true);
                }
                LambdaQueryWrapper<BlogComments> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(BlogComments::getParentId, blogCommentDTO.getId());
                queryWrapper1.orderByDesc(BlogComments::getCreateTime);
                List<BlogComments> blogComments1 = blogCommentsMapper.selectList(queryWrapper1);
                if (!blogComments1.isEmpty()) {
                    List<BlogComments> collect2 = blogComments1.stream().map(blogComments2 -> {
                        Long userBlogId = blogComments2.getUserId();
                        User user = userMapper.selectById(userBlogId);
                        blogComments2.setAvatarUrl(user.getAvatarUrl());
                        blogComments2.setUsername(user.getUsername());
                        return blogComments2;
                    }).collect(Collectors.toList());
                    blogCommentDTO.setChildren(collect2);
                } else {
                    blogCommentDTO.setChildren(Collections.emptyList());
                }
                return blogCommentDTO;
            }).collect(Collectors.toList());
            return new Result(SUCCESS, "", collect1);
        }
    }

    @Override
    public Result saveBlogComments(BlogComments blogComments) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        User user = userMapper.selectById(userId);
        blogComments.setUserId(userId);
        try {
            save(blogComments);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR,"评论字数过多请适当减少");
        }
        //将前端必要信息封装返回
        BlogCommentDTO blogCommentDTO = BeanUtil.copyProperties(blogComments, BlogCommentDTO.class);
        blogCommentDTO.setChildren(Collections.emptyList());
        blogCommentDTO.setLiked(0);
        blogCommentDTO.setUsername(user.getUsername());
        blogCommentDTO.setAvatarUrl(user.getAvatarUrl());
        blogCommentDTO.setParentId(0L);
        blogCommentDTO.setIsLike(false);
        return new Result(SUCCESS, "评论成功",blogCommentDTO);
    }

    @Override
    public Result replyBlogComments(BlogComments blogComments) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        User user = userMapper.selectById(userId);
        blogComments.setUserId(userId);
        try {
            save(blogComments);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR,"评论字数过多请适当减少");
        }
        //将前端必要信息封装返回
        blogComments.setLiked(0);
        blogComments.setUsername(user.getUsername());
        blogComments.setAvatarUrl(user.getAvatarUrl());
        return new Result(SUCCESS, "评论成功",blogComments);
    }

    @Transactional
    @Override
    public Result likeBlogComments(Long id) {
        LoginUser principal = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        String key = RedisConstans.LIKE_BLOG_COMMENTS_KEY + userId;
        Double score = stringRedisTemplate.opsForZSet().score(key, id.toString());
        if (score == null) {
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, id.toString(), System.currentTimeMillis());
            }
        } else {
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, id.toString());
            }
        }
        return new Result(SUCCESS, "");
    }
}
