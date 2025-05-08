package com.example.springboot.service;

import com.example.springboot.entity.ArticleComment;
import com.example.springboot.mapper.ArticleCommentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArticleCommentService {
    @Resource
    private ArticleCommentMapper articleCommentMapper;

    public List<ArticleComment> getCommentsByArticleId(Integer articleId) {
        return articleCommentMapper.selectByArticleId(articleId);
    }

    public void addComment(ArticleComment comment) {
        comment.setCreateTime(LocalDateTime.now());
        articleCommentMapper.insert(comment);
    }

    public void deleteComment(Integer id) {
        articleCommentMapper.deleteById(id);
    }

    public Integer getCommentCount(Integer articleId) {
        return articleCommentMapper.countByArticleId(articleId);
    }
} 