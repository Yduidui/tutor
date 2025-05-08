package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.service.*;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/adminData")
public class AdminDataController {

    @Resource
    private CourseService courseService;
    @Resource
    private AppointmentService appointmentService;
    @Resource
    private StudentService studentService;
    @Resource
    private OrderService orderService;
    @Resource
    private TeacherService teacherService;
    @Resource
    private PageViewService pageViewService;
    @Resource
    private AdminService adminService;
    @Resource
    private ArticleService articleService;
    @Resource
    private NewsService newsService;
    @Resource
    private HelpService helpService;


    /**
     * 课程总数
     */
    @GetMapping("/getAllVisitCount")
    public Result getAllVisitCount() {
        int count = pageViewService.getAllVisitCount(); // 你已有的方法
        return Result.success(count);
    }

    /**
     * 课程总数
     */
    @GetMapping("/getAllCourseCount")
    public Result getAllCourseCount() {
        int count = courseService.getAllCourseCount(); // 你已有的方法
        return Result.success(count);
    }

    /**
     * 用户总数
     */
    @GetMapping("/getAllUserCount")
    public Result getAllUserCount() {
        int studentCount = studentService.getAllStudentCount(); // 你已有的方法
        int teacherCount = teacherService.getAllTeacherCount(); // 你已有的方法
        int totalUser = studentCount + teacherCount;
        return Result.success(totalUser);
    }

    /**
     * 订单总数
     */
    @GetMapping("/getAllOrderCount")
    public Result getAllOrderCount() {
        int count = orderService.getAllOrderCount(); // 你已有的方法

        return Result.success(count);
    }

    @GetMapping("/getLineData")
    public Result getLineData(@RequestParam String timeRange) {
        Map<String, Object> result = new HashMap<>();
        result.put("teacher", teacherService.getTrendDataAsMap(timeRange));
        result.put("student", studentService.getTrendDataAsMap(timeRange));
        result.put("order", orderService.getTrendDataAsMap(timeRange));
        result.put("visit", pageViewService.getTrendDataAsMap(timeRange));
        return Result.success(result);
    }

    @GetMapping("/getUserPieData")
    public Result getUserPieData() {
        Map<String, Integer> map = new HashMap<>();
        map.put("student", studentService.getAllStudentCount());
        map.put("teacher", teacherService.getAllTeacherCount());
        map.put("admin", adminService.getAllAdminCount());
        return Result.success(map);
    }

    @GetMapping("/getVisitBarData")
    public Result getVisitBarData() {
        List<Map<String, Object>> list = pageViewService.getThisWeekVisitData();
        return Result.success(list);
    }


    @GetMapping("/getRecentOrders")
    public Result getRecentOrders() {
        List<Order> list = orderService.getRecentOrders(5);
        return Result.success(list);
    }


    @GetMapping("/getLatestUsers")
    public Result getLatestUsers() {
        List<Student> studentList = studentService.getRecentStudents(5);
        List<Teacher> teacherList = teacherService.getRecentTeachers(5);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Student s : studentList) {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar", s.getAvatar());
            map.put("name", s.getName());
            map.put("role", "学生");
            map.put("registrationTime", s.getRegistrationTime()); // 或 registrationTime
            result.add(map);
        }

        for (Teacher t : teacherList) {
            Map<String, Object> map = new HashMap<>();
            map.put("avatar", t.getAvatar());
            map.put("name", t.getName());
            map.put("role", "老师");
            map.put("registrationTime", t.getRegistrationTime());
            result.add(map);
        }

        // 按 registrationTime 降序排列，并取前 5 个
        result.sort((a, b) -> ((Date) b.get("registrationTime")).compareTo((Date) a.get("registrationTime")));
        List<Map<String, Object>> latestFive = result.stream().limit(5).collect(Collectors.toList());

        return Result.success(latestFive);
    }


    @GetMapping("/getPopularArticles")
    public Result getPopularArticles() {
        List<Article> list = articleService.getPopularArticles(5);
        return Result.success(list);
    }

    @GetMapping("/getAnnouncements")
    public Result getAnnouncements() {
        List<News> list = newsService.getAnnouncements(5);
        return Result.success(list);
    }

    @GetMapping("/getLatestHelps")
    public Result getLatestHelps() {
        List<Help> list = helpService.getLatestHelps(5);
        return Result.success(list);
    }


}
