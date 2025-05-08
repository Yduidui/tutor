package com.example.springboot.mapper;

import com.example.springboot.entity.Subject;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SubjectMapper {
    List<Subject> selectAll(Subject subject);

    // @Select("SELECT * FROM subject WHERE id = #{id}")  // 注解方式
    Subject selectById(Integer id);

    void insert(Subject subject);

    void updateById(Subject subject);

    void deleteById(Integer id);
}