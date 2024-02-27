package com.huayu.controller;


import com.huayu.domain.Blog;
import com.huayu.dto.Result;
import com.huayu.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * blog表现层对象
 */
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private IBlogService blogService;

    /**
     * 新增一篇文章
     *
     * @param blog 文章信息
     */
    @PostMapping
    public Result saveBlog(@RequestBody Blog blog) {
        return blogService.saveBlog(blog);
    }

    /**
     * 查询一篇文章的具体信息
     *
     * @param id 文章的id
     * @return 文章的信息
     */
    @GetMapping("/{id}")
    public Result getBlog(@PathVariable("id") Long id) {
        return blogService.getBlog(id);
    }

    /**
     * 点赞一篇文章，或取消点赞
     *
     * @param id 文章的id
     */
    @PutMapping("/like/{id}")
    public Result likeBlog(@PathVariable("id") Long id) {
        return blogService.likeBlog(id);
    }

    /**
     * 查询我发布的文章
     *
     * @return 所有登录用户发布的文章信息
     */
    @GetMapping("/of/me")
    public Result getMyBlogs() {
        return blogService.getMyBlogs();
    }

    /**
     * 查询我喜欢的文章
     *
     * @return 所有登录用户喜欢的文章信息
     */
    @GetMapping("/like/me")
    public Result getMyLike() {
        return blogService.getMyLike();
    }

    /**
     * 滚动查询最新的文章
     *
     * @param max    上次查询的最小文章时间
     * @param offset 偏移量
     * @return 最新的文章
     */
    @GetMapping("/new")
    public Result getNewBlog(@RequestParam("lastId") Long max, @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        return blogService.getNewBlog(max, offset);
    }

    /**
     * 分页查询最热门的文章
     *
     * @param current 页数
     * @return 最热门的文章
     */
    @GetMapping("/hot")
    public Result getHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return blogService.getHotBlog(current);
    }

}
