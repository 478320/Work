package com.huayu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.huayu.domain.BlogComments;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论数据的返回对象
 */
@Data
@AllArgsConstructor
public class BlogCommentDTO {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Long blogId;

    private Long parentId;

    private String content;

    private Integer liked;

    @JsonFormat(pattern = "yyyy.M.d HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String avatarUrl;

    private String username;

    private Boolean isLike;

    private List<BlogComments> children;
}
