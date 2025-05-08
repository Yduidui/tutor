package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Comment;
import com.example.springboot.entity.Comment;
import com.example.springboot.entity.Teacher;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.CommentMapper;
import com.example.springboot.mapper.TeacherMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;
    @Resource
    private TeacherMapper teacherMapper;

    public void add(Comment comment) {
        // 设置创建时间为当前时间
        comment.setCreatedTime(LocalDateTime.now());
        // 插入课程信息到数据库
        commentMapper.insert(comment);
    }


    public void update(Comment comment) {
        commentMapper.updateById(comment);
    }

    public void updateIsDeleted(Integer id) {
        commentMapper.updateIsDeleted(id);
    }

    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }
    public List<Comment> selectAll(String keyword, Comment comment) {
        return commentMapper.selectAll(keyword, comment);
    }

    public Comment selectById(Integer id) {
        Comment comment =  commentMapper.selectById(id);
        return comment;
    }

    public PageInfo<Comment> selectPage(String keyword, Comment comment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(keyword, comment);
        return PageInfo.of(list);
    }
    public void submitComment(Comment comment) {
        // 检查是否已经评价过
        if (commentMapper.checkDuplicate(comment.getTeacherId(), comment.getStudentId()) > 0) {
            throw new CustomerException("500", "您已经评价过该教师");
        }

        // 插入评论
        commentMapper.insert(comment);

        // 查询新的平均分
        Double rating = commentMapper.selectAverageRating(comment.getTeacherId());

        // 只更新评分字段
        teacherMapper.updateRating(comment.getTeacherId(), rating);
    }

    public List<Comment> getTeacherComments(Integer teacherId) {
        return commentMapper.selectByTeacherId(teacherId);
    }

    public List<Comment> getStudentComments(Integer studentId) {
        return commentMapper.selectByStudentId(studentId);
    }

    public Double getAverageRating(Integer teacherId) {
        return commentMapper.selectAverageRating(teacherId);
    }

    public void softDeleteComment(Integer id) {
        commentMapper.softDelete(id);
    }

    public void restoreComment(Integer id) {
        commentMapper.restore(id);
    }

    public void deleteComment(Integer id) {
        commentMapper.delete(id);
    }

    public List<Map<String, Object>> getRatingDistribution(Integer teacherId) {
        return commentMapper.getRatingDistribution(teacherId);
    }

    public List<Map<String, Object>> getCommentTrend(Integer teacherId) {
        return commentMapper.getCommentTrend(teacherId);
    }

    public int getCommentCount(Integer teacherId) {
        return commentMapper.getCommentCount(teacherId);
    }
}