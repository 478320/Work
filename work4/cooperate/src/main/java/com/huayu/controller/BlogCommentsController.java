package com.huayu.controller;


import com.huayu.domain.BlogComments;
import com.huayu.dto.Result;
import com.huayu.service.IBlogCommentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * blogComments实体类对象
 */
@RestController
@RequestMapping("/blog-comments")
public class BlogCommentsController {

    @Autowired
    private IBlogCommentsService blogCommentsService;

    /**
     * 查询某一文章下的全部评论
     *
     * @param id 文章的id
     * @return 关于该文章的所有评论信息
     */
    @GetMapping("/{id}")
    public Result getBlogComments(@PathVariable Long id) {
        return blogCommentsService.getBlogComments(id);
    }

    /**
     * 为文章添加一条评论
     *
     * @param blogComments 评论内容
     * @return 评论的信息
     */
    @PostMapping
    public Result saveBlogComments(@RequestBody BlogComments blogComments) {
        return blogCommentsService.saveBlogComments(blogComments);
    }

    /**
     * 回复文章的某条评论
     *
     * @param blogComments 回复某条评论
     * @return 评论的信息
     */
    @PostMapping("/reply")
    public Result replyBlogComments(@RequestBody BlogComments blogComments) {
        return blogCommentsService.replyBlogComments(blogComments);
    }

    /**
     * 点赞评论
     *
     * @param id 要点赞的id
     */
    @PutMapping("/like/{id}")
    public Result likeBlogComments(@PathVariable("id") Long id) {
        return blogCommentsService.likeBlogComments(id);
    }

}
