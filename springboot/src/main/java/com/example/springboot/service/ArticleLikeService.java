package com.example.springboot.service;

import com.example.springboot.entity.ArticleLike;
import com.example.springboot.mapper.ArticleLikeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ArticleLikeService {
    @Resource
    private ArticleLikeMapper articleLikeMapper;

    @Transactional
    public boolean toggleLike(Integer articleId, Integer userNo) {
        ArticleLike existingLike = articleLikeMapper.selectByArticleAndUser(articleId, userNo);
        if (existingLike != null) {
            // 取消点赞
            articleLikeMapper.delete(articleId, userNo);
            articleLikeMapper.updateLikeCount(articleId, -1); // likes - 1
            return false;
        } else {
            // 点赞
            ArticleLike newLike = new ArticleLike();
            newLike.setArticleId(articleId);
            newLike.setUserNo(userNo);
            newLike.setCreateTime(LocalDateTime.now());
            articleLikeMapper.insert(newLike);
            articleLikeMapper.updateLikeCount(articleId, 1); // likes + 1
            return true;
        }
    }


    public Integer getLikeCount(Integer articleId) {
        return articleLikeMapper.countByArticleId(articleId);
    }

    public boolean isArticleLikedByUser(Integer articleId, Integer userNo) {
        return articleLikeMapper.countByArticleIdAndUserNo(articleId, userNo) > 0;
    }
}