package com.example.springboot.mapper;

import com.example.springboot.entity.Order;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface TeacherMapper {
    List<Teacher> selectAll(Teacher teacher);

    Teacher selectById(Integer id);

    void insert(Teacher teacher);

    void updateById(Teacher teacher);
    void updateRating(@Param("id") Integer id, @Param("rating") Double rating);
    void deleteById(Integer id);

    @Select("select * from teacher where username = #{username}")
    Teacher selectByUsername(String username);

    List<Teacher> searchTeachers(@Param("keyword") String keyword);

    int getAllTeacherCount();

    List<Map<String, Object>> getTrendGroupedByDate(@Param("timeRange") String timeRange);

    List<Teacher> getRecentTeachers(@Param("limit") int limit);

    List<Teacher> selectTopTeachers();
}