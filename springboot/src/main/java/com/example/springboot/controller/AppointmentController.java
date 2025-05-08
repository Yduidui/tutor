package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.*;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.service.AppointmentService;
import com.example.springboot.service.StudentService;
import com.example.springboot.service.TeacherService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Resource
    private AppointmentService appointmentService;

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    // 新增预约时间段
    @PostMapping("/add")
    public Result add(@RequestBody Appointment appointment) {
        appointmentService.add(appointment);
        return Result.success();
    }

    // 预约时间段
    @PostMapping("/book/{id}")
    public Result bookSlot(@PathVariable Integer id, @RequestParam Integer studentId) {
        appointmentService.bookSlot(id, studentId);
        return Result.success();
    }

    @PostMapping("/cancel/{id}")
    public Result cancelBooking(@PathVariable Integer id,
                                @RequestParam Integer accountId,
                                @RequestParam String role) {
        // 根据 accountId 和 role 获取学生或教师信息
        Account account = null;

        if ("STU".equals(role)) {
            // 查询学生信息
            Student student = studentService.selectById(accountId);
            if (student != null) {
                account = student;  // 账户是学生
            }
        } else if ("TEA".equals(role)) {
            // 查询教师信息
            Teacher teacher = teacherService.selectById(accountId);
            if (teacher != null) {
                account = teacher;  // 账户是教师
            }
        }

        // 如果账户信息为空，抛出异常
        if (account == null) {
            throw new CustomerException("404", "账户不存在");
        }

        // 根据角色取消预约
        if ("STU".equals(account.getRole())) {
            // 学生角色
            appointmentService.cancelBooking(id, account);  // 传递学生对象
        } else if ("TEA".equals(account.getRole())) {
            // 教师角色
            appointmentService.cancelBooking(id, account);  // 传递教师对象
        } else {
            // 如果角色无效
            throw new CustomerException("500", "无效的角色");
        }

        return Result.success("取消预约成功");
    }




    // 删除预约时间段
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        appointmentService.deleteById(id);
        return Result.success();
    }

    // 批量删除预约时间段
    @DeleteMapping("/deleteByCourse/{courseId}")
    public Result deleteByCourseId(@PathVariable Integer courseId) {
        appointmentService.deleteByCourseId(courseId);
        return Result.success();
    }

    // 查询所有预约
    @GetMapping("/selectAll")
    public Result selectAll(Appointment appointment) {
        List<Appointment> list = appointmentService.selectAll(appointment);
        return Result.success(list);
    }

    // 根据ID查询预约
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Appointment appointment = appointmentService.selectById(id);
        return Result.success(appointment);
    }

    // 根据课程ID查询预约
    @GetMapping("/selectByCourseId/{courseId}")
    public Result selectByCourseId(@PathVariable Integer courseId) {
        List<Appointment> list = appointmentService.selectByCourseId(courseId);
        return Result.success(list);
    }

    // 根据教师ID查询预约
    @GetMapping("/selectByTeacherId/{teacherId}")
    public Result selectByTeacherId(@PathVariable Integer teacherId) {
        List<Appointment> list = appointmentService.selectByTeacherId(teacherId);
        return Result.success(list);
    }

    // 根据学生ID查询预约
    @GetMapping("/selectByStudentId/{studentId}")
    public Result selectByStudentId(@PathVariable Integer studentId) {
        List<Appointment> list = appointmentService.selectByStudentId(studentId);
        return Result.success(list);
    }

    // 查询可用的预约时间段
    @GetMapping("/selectAvailableSlots/{courseId}")
    public Result selectAvailableSlots(@PathVariable Integer courseId) {
        List<Appointment> list = appointmentService.selectAvailableSlots(courseId);
        return Result.success(list);
    }

    // 分页查询
    @GetMapping("/selectPage")
    public Result selectPage(
            Appointment appointment,
            @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Appointment> page = appointmentService.selectPage(appointment,pageNum, pageSize);
        return Result.success(page);
    }



} 