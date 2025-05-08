package com.example.springboot.mapper;


import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface StudentMapper {
    List<Student> selectAll(Student student);

//    @Select("select * from student where id = #{id}")  注解方式
    Student selectById(Integer id);

    void insert(Student student);

    void updateById(Student student);

    void deleteById(Integer id);

    @Select("select * from student where username = #{username}")
    Student selectByUsername(String username);


    List<Student> searchStudents(@Param("keyword") String keyword);

    int getAllStudentCount();

    List<Map<String, Object>> getTrendGroupedByDate(@Param("timeRange") String timeRange);

    List<Student> getRecentStudents(@Param("limit") int limit);
}
