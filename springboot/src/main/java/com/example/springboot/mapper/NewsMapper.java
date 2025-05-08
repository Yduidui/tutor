package com.example.springboot.mapper;

import com.example.springboot.entity.Article;
import com.example.springboot.entity.News;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface NewsMapper {

    // 查询所有（带搜索与类型筛选）
    List<News> selectAll(News news);

    // 根据 ID 查询
    News selectById(Integer id);

    // 新增
    int insert(News news);

    // 修改
    int updateById(News news);

    // 删除
    int deleteById(Integer id);


    @Select("select * from news where title = #{title}")
    News selectByTitle(String title);

    @Select("select count(*) from article where time like'%${date}%'")
    Integer selectCountByDate(String date);

    List<News> getAnnouncements(@Param("limit") int limit);
}
