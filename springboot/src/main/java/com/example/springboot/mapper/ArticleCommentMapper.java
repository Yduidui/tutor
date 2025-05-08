package com.example.springboot.mapper;

import com.example.springboot.entity.ArticleComment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ArticleCommentMapper {
    List<ArticleComment> selectByArticleId(Integer articleId);

    void insert(ArticleComment comment);

    void deleteById(Integer id);

    Integer countByArticleId(Integer articleId);
} 