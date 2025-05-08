package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.service.ArticleLikeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleLikeController {
    @Resource
    private ArticleLikeService articleLikeService;

    @PostMapping("/like")
    public Result toggleLike(@RequestBody ArticleLikeRequest request) {
        boolean isLiked = articleLikeService.toggleLike(request.getArticleId(), request.getUserNo());
        return Result.success(isLiked);
    }

    @GetMapping("/likes/{articleId}")
    public Result getLikeCount(@PathVariable Integer articleId) {
        Integer count = articleLikeService.getLikeCount(articleId);
        return Result.success(count);
    }

    @GetMapping("/like/check/{articleId}/{userNo}")
    public Result checkIfLiked(@PathVariable Integer articleId, @PathVariable Integer userNo) {
        boolean isLiked = articleLikeService.isArticleLikedByUser(articleId, userNo);
        return Result.success(isLiked); // 返回的是 Object 类型，布尔值也可以
    }


}

class ArticleLikeRequest {
    private Integer articleId;
    private Integer userNo;

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }
} 