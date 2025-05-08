package com.example.springboot.mapper;

import com.example.springboot.entity.Comment;
import com.example.springboot.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
    List<Comment> selectAll(@Param("keyword") String keyword,Comment comment);

    //    @Select("select * from comment where id = #{id}")  注解方式
    Comment selectById(Integer id);


    void updateById(Comment comment);

    void updateIsDeleted(Integer id);

    void deleteById(Integer id);

    void insert(Comment comment);

    List<Comment> selectByTeacherId(Integer teacherId);

    List<Comment> selectByStudentId(Integer studentId);

    Double selectAverageRating(Integer teacherId);

    Integer checkDuplicate(@Param("teacherId") Integer teacherId, @Param("studentId") Integer studentId);


    void softDelete(Integer id);

    void restore(Integer id);

    void delete(Integer id);

    List<Map<String, Object>> getRatingDistribution(Integer teacherId);

    List<Map<String, Object>> getCommentTrend(Integer teacherId);

    int getCommentCount(Integer teacherId);
}