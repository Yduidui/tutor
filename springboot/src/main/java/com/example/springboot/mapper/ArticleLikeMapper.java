package com.example.springboot.mapper;

import com.example.springboot.entity.ArticleLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface ArticleLikeMapper {
    ArticleLike selectByArticleAndUser(@Param("articleId") Integer articleId, @Param("userNo") Integer userNo);

    void insert(ArticleLike articleLike);

    void delete(@Param("articleId") Integer articleId, @Param("userNo") Integer userNo);

    Integer countByArticleId(Integer articleId);

    int countByArticleIdAndUserNo(@Param("articleId") Integer articleId, @Param("userNo") Integer userNo);

    void updateLikeCount(@Param("articleId") Integer articleId, @Param("delta") int delta);

}