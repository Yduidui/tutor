package com.example.springboot.mapper;

import com.example.springboot.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper {
    List<Article> selectAll(Article article);
    Article selectById(Integer id);
    void insert(Article article);
    void updateById(Article article);
    void deleteById(Integer id);
    Article selectByTitle(String title);
    Integer selectCountByDate(String date);
    List<Article> selectByUserNo(Integer userNo);
    Integer getCommentCount(Integer articleId);
    Integer checkUserLiked(@Param("articleId") Integer articleId, @Param("userNo") Integer userNo);

    List<Article> getPopularArticles(@Param("limit") int limit);
}