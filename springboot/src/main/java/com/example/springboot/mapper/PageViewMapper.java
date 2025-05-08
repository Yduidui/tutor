package com.example.springboot.mapper;

import com.example.springboot.entity.PageViewLog;
import com.example.springboot.entity.PageViewLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface PageViewMapper {
   
    // 获取指定日期范围的访问量统计
    List<Map<String, Object>> getViewsByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
   
    List<PageViewLog> selectAll(PageViewLog pageViewLog);

    //    @Select("select * from pageViewLog where id = #{id}")  注解方式
    PageViewLog selectById(Integer id);

    void insert(PageViewLog pageViewLog);

    void updateById(PageViewLog pageViewLog);

    void deleteById(Integer id);

    void insertBatch(@Param("list") List<PageViewLog> list);

    List<Map<String, Object>> getTrendGroupedByDate(@Param("timeRange") String timeRange);

    int getAllVisitCount();

    int getVisitCountByDate(@Param("date") LocalDate date);
} 