package com.example.springboot.controller;


import com.example.springboot.common.Result;
import com.example.springboot.service.AppointmentService;
import com.example.springboot.service.CommentService;
import com.example.springboot.service.CourseService;
import com.example.springboot.service.StudentService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teacherData")
public class TeacherDataController {
    @Resource
    private CourseService courseService;
    @Resource
    private CommentService commentService;
    @Resource
    private AppointmentService appointmentService;
    @Resource
    private StudentService studentService;

    /**
     * 教师课程总数
     */
    @GetMapping("/getCourseCount/{teacherId}")
    public Result getCourseCount(@PathVariable Integer teacherId) {
        int count = courseService.countByTeacherId(teacherId); // 你已有的方法
        return Result.success(count);
    }

    /**
     * 教师评论总数
     */
    @GetMapping("/getCommentCount/{teacherId}")
    public Result getCommentCount(@PathVariable Integer teacherId) {
        int count = commentService.getCommentCount(teacherId); // 你已有的方法
        return Result.success(count);
    }

    /**
     * 教师订单总数
     */
    @GetMapping("/getTotalAppointments/{teacherId}")
    public Result getTotalAppointments(@PathVariable Integer teacherId) {
        int count = appointmentService.getTotalAppointments(teacherId); // 你已有的方法
        return Result.success(count);
    }


    /**
     * 教师平均评分
     */
    @GetMapping("/getAverageRating/{teacherId}")
    public Result getAverageRating(@PathVariable Integer teacherId) {
        Double count = commentService.getAverageRating(teacherId); // 你已有的方法
        return Result.success(count);
    }

    /**
     * 获取评分分布（不同评级的评论数量）
     */
    @GetMapping("/getRatingDistribution/{teacherId}")
    public Result getRatingDistribution(@PathVariable Integer teacherId) {
        List<Map<String, Object>> distribution = commentService.getRatingDistribution(teacherId);
        return Result.success(distribution);
    }


    /**
     * 获取订单分布（不同评级的评论数量）
     */
    @GetMapping("/getAppointmentDistribution/{teacherId}")
    public Result getAppointmentDistribution(@RequestParam("timeRange") String timeRange, @PathVariable Integer teacherId) {
        List<Map<String, Object>> distribution = appointmentService.getAppointmentDistribution(teacherId, timeRange);
        return Result.success(distribution);
    }

}
