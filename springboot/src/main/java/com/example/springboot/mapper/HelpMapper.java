package com.example.springboot.mapper;


import com.example.springboot.entity.Help;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface HelpMapper {
    List<Help> selectAll(Help help);

//    @Select("select * from help where id = #{id}")  注解方式
    Help selectById(Integer id);

    void insert(Help help);

    void updateById(Help help);

    void replyHelp(Help help);

    void deleteById(Integer id);

    @Select("select * from help where title = #{title}")
    Help selectByTitle(String title);

    List<Help> searchHelps(@Param("keyword") String keyword, 
                          @Param("priority") Integer priority,
                          @Param("status") Integer status);

    int getAllHelpCount();

    List<Map<String, Object>> getTrendGroupedByDate(@Param("timeRange") String timeRange);


    List<Help> getLatestHelps(@Param("limit")  int limit);
}
