package com.huayu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huayu.domain.Blog;
import com.huayu.dto.*;
import com.huayu.domain.User;
import com.huayu.exception.BusinessException;
import com.huayu.mapper.BlogMapper;
import com.huayu.mapper.UserMapper;
import com.huayu.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huayu.utils.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.huayu.utils.Code.FAIL_NULL;
import static com.huayu.utils.Code.SUCCESS;
import static com.huayu.utils.RedisConstans.LIKE_BLOG_KEY;
import static com.huayu.utils.RedisConstans.NEW_BLOG_KEY;

/**
 * blog服务层自定义实现类
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Autowired
    private BlogMapper blogMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result saveBlog(Blog blog) {
        LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        blog.setUserId(userId);
        try {
            blogMapper.insert(blog);
        } catch (Exception e) {
            throw new BusinessException(Code.BUSINESS_ERR,"文章或标题长度过长");
        }
        stringRedisTemplate.opsForZSet().add(NEW_BLOG_KEY,blog.getId().toString(),System.currentTimeMillis());
        return new Result(SUCCESS,"新增文章成功");
    }

    @Transactional
    @Override
    public Result getBlog(Long id) {
        Blog blog = blogMapper.selectById(id);
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser"){
            blog.setIsLike(false);
        }else {
            LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Long userId = principal.getUser().getId();
            if (blog == null){
                return new Result(FAIL_NULL,"要查找的文章不存在");
            }
            String key = "blog:views:" + id;
            //登录情况下查看文章，文章查看量加一
            Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
            if (score == null) {
                boolean isSuccess = update().setSql("views = views + 1").eq("id", id).update();
                if (isSuccess) {
                    stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
                }
            }
            //查询该用户是否点赞过该文章
            Double scoreLike = stringRedisTemplate.opsForZSet().score(LIKE_BLOG_KEY + userId, id.toString());
            if (scoreLike == null){
                blog.setIsLike(false);
            }else {
                blog.setIsLike(true);
            }
        }
        //将前端必要信息返回
        Long blogUserId = blog.getUserId();
        User user = userMapper.selectById(blogUserId);
        blog.setUsername(user.getUsername());
        blog.setAvatarUrl(user.getAvatarUrl());
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getUserId,blogUserId);
        Long count = blogMapper.selectCount(queryWrapper);
        return new Result(SUCCESS,"",new SelectBlogDTO(blog,count));
    }

    @Transactional
    @Override
    public Result likeBlog(Long id) {
        LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        String key = LIKE_BLOG_KEY + userId;
        Double score = stringRedisTemplate.opsForZSet().score(key, id.toString());
        //未点赞过该文章则文章点赞量加一，并将点赞信息存储到redis中
        if (score == null) {
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, id.toString(), System.currentTimeMillis());
            }
            //点赞过该文章则文章点赞量减一，并将点赞信息在redis中删除
        } else {
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, id.toString());
            }
        }
        return new Result(SUCCESS,"");
    }

    @Override
    public Result getMyBlogs() {
        LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        User user = userMapper.selectById(userId);
        LambdaQueryWrapper<Blog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Blog::getUserId,userId);
        List<Blog> blogs = blogMapper.selectList(queryWrapper);
        if (blogs.isEmpty() || blogs==null){
            return new Result(SUCCESS,"",Collections.emptyList());
        }
        List<Blog> collect = blogs.stream().map(blog -> {
            blog.setUsername(user.getUsername());
            blog.setAvatarUrl(user.getAvatarUrl());
            // 查询各篇文章我是否点过赞
            Double scoreLike = stringRedisTemplate.opsForZSet().score(LIKE_BLOG_KEY + userId, blog.getId().toString());
            if (scoreLike == null) {
                blog.setIsLike(false);
            } else {
                blog.setIsLike(true);
            }
            return blog;
        }).collect(Collectors.toList());
        BlogDTO blogDTO = new BlogDTO(collect, collect.size());
        return new Result(SUCCESS,"",blogDTO);
    }

    @Override
    public Result getMyLike() {
        LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        //从redis中获取我点赞过文章的id
        Set<String> range = stringRedisTemplate.opsForZSet().range(LIKE_BLOG_KEY + userId, 0, -1);
        if (range.isEmpty() || range ==null){
            return new Result(SUCCESS,"",new BlogDTO(Collections.emptyList(),0));
        }
        List<Blog> blogs = blogMapper.selectBatchIds(range);
        List<Blog> collect = blogs.stream().map(blog -> {
            Long blogUserId = blog.getUserId();
            User user = userMapper.selectById(blogUserId);
            blog.setUsername(user.getUsername());
            blog.setAvatarUrl(user.getAvatarUrl());
            blog.setIsLike(true);
            return blog;
        }).collect(Collectors.toList());
        return new Result(SUCCESS,"",new BlogDTO(collect,collect.size()));
    }

    @Override
    public Result getNewBlog(Long max, Integer offset) {
        //查询最新的十条文章的id
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(NEW_BLOG_KEY, 0, max, offset, 10);
        if (typedTuples.isEmpty() || typedTuples == null){
            return new Result(SUCCESS,"",new ScrollResult(Collections.EMPTY_LIST,null,null));
        }
        long minTime = 0;
        int os = 1;
        //更具查询到的文章信息修改偏移量和最后一次查找到的文章
        List<Long> ids = new ArrayList<>(typedTuples.size());
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            ids.add(Long.valueOf(typedTuple.getValue()));
            long time = typedTuple.getScore().longValue();
            if (time == minTime){
                os++;
            }else {
                minTime = time;
                os = 1;
            }
        }
        //按顺序查找文章的具体信息
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogs = query().in("id", ids)
                .last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Blog blog : blogs) {
            queryBlogUser(blog);
            isBlogLiked(blog);
        }
        //封装对应的信息返回给前端
        ScrollResult r = new ScrollResult();
        r.setList(blogs);
        r.setOffset(os);
        r.setMinTime(minTime);
        return new Result(SUCCESS,"",r);
    }

    /**
     * 查询文章的用户信息
     * @param blog 文章信息
     */
    private void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userMapper.selectById(userId);
        blog.setUsername(user.getUsername());
        blog.setAvatarUrl(user.getAvatarUrl());
    }

    /**
     * 查询用户是否喜欢过该篇文章
     * @param blog 文章信息
     */
    private void isBlogLiked(Blog blog) {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() == "anonymousUser"){
            blog.setIsLike(false);
            return;
        }
        LoginUser principal = (LoginUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUser().getId();
        String key = LIKE_BLOG_KEY + userId;
        Double score = stringRedisTemplate.opsForZSet().score(key, blog.getId().toString());
        blog.setIsLike(score != null);
    }

    @Override
    public Result getHotBlog(Integer current) {
        IPage page1 = new Page(current,10);
        QueryWrapper<Blog> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("views");
        IPage page = blogMapper.selectPage(page1, queryWrapper);
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        if (records.isEmpty() || records == null){
            return new Result(SUCCESS,"",Collections.emptyList());
        }
        // 查询用户
        records.forEach(blog -> {
            this.queryBlogUser(blog);
            this.isBlogLiked(blog);
        });
        return new Result(SUCCESS,"",records);
    }
}
