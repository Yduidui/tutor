package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.ArticleComment;
import com.example.springboot.service.ArticleCommentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleCommentController {
    @Resource
    private ArticleCommentService articleCommentService;

    @GetMapping("/comments/{articleId}")
    public Result getComments(@PathVariable Integer articleId) {
        List<ArticleComment> comments = articleCommentService.getCommentsByArticleId(articleId);
        return Result.success(comments);
    }

    @PostMapping("/comment")
    public Result addComment(@RequestBody ArticleComment comment) {
        articleCommentService.addComment(comment);
        return Result.success();
    }

    @DeleteMapping("/comment/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        articleCommentService.deleteComment(id);
        return Result.success();
    }

//    @GetMapping("/comment/count/{articleId}")
//    public Result getCommentCount(@PathVariable Integer articleId) {
//        Integer count = articleCommentService.getCommentCount(articleId);
//        return Result.success(count);
//    }
} 