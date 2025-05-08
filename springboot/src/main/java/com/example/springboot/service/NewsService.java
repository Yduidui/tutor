package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Article;
import com.example.springboot.entity.News;
import com.example.springboot.mapper.NewsMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsService {

    @Resource
    private NewsMapper newsMapper;

    public void add(News news) {
        LocalDateTime date = DateUtil.date().toLocalDateTime();
        news.setCreateTime(date);
        news.setUpdateTime(date);
        newsMapper.insert(news);
    }

    public void deleteById(Integer id) {
        newsMapper.deleteById(id);
    }

    public void update(News news) {
        LocalDateTime date = DateUtil.date().toLocalDateTime();
        news.setUpdateTime(date);
        newsMapper.updateById(news);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            newsMapper.deleteById(id);
        }
    }

    public List<News> selectAll(News news) {
        return newsMapper.selectAll(news);
    }

    public News selectById(Integer id) {
        return newsMapper.selectById(id);
    }

    public PageInfo<News> selectPage(News news, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<News> list = newsMapper.selectAll(news);
        return PageInfo.of(list);
    }

    public List<News> getAnnouncements(int limit) {
        return newsMapper.getAnnouncements(limit);
    }
}
