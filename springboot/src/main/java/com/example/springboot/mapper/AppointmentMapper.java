package com.example.springboot.mapper;

import com.example.springboot.entity.Appointment;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface AppointmentMapper {
    
    // 查询所有预约
    List<Appointment> selectAll(Appointment appointment);
    
    // 根据ID查询预约
    Appointment selectById(Integer id);
    
    // 根据课程ID查询预约
    List<Appointment> selectByCourseId(Integer courseId);
    
    // 根据教师ID查询预约
    List<Appointment> selectByTeacherId(Integer teacherId);
    
    // 根据学生ID查询预约
    List<Appointment> selectByStudentId(Integer studentId);
    
    // 查询可用的预约时间段
    List<Appointment> selectAvailableSlots(Integer courseId);
    
    // 新增预约时间段
   void insert(Appointment appointment);
    
    // 更新预约状态
    void updateStatus(@Param("id") Integer id, @Param("studentId") Integer studentId, @Param("status") String status);

    // 删除预约时间段
    void deleteById(Integer id);
    
    // 批量删除预约时间段
    void deleteByCourseId(Integer courseId);

    int getTotalAppointments(Integer teacherId);

    List<Map<String, Object>> getAppointmentDistribution(@Param("teacherId") Integer teacherId,  @Param("timeRange") String timeRange);
} 