package com.example.springboot.service;

import com.example.springboot.entity.Account;
import com.example.springboot.entity.Appointment;
import com.example.springboot.entity.Course;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.AppointmentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    @Resource
    private AppointmentMapper appointmentMapper;

    // 查询所有预约
    public List<Appointment> selectAll(Appointment appointment) {
        return appointmentMapper.selectAll(appointment);
    }

    // 根据ID查询预约
    public Appointment selectById(Integer id) {
        return appointmentMapper.selectById(id);
    }

    // 根据课程ID查询预约
    public List<Appointment> selectByCourseId(Integer courseId) {
        return appointmentMapper.selectByCourseId(courseId);
    }

    // 根据教师ID查询预约
    public List<Appointment> selectByTeacherId(Integer teacherId) {
        return appointmentMapper.selectByTeacherId(teacherId);
    }

    // 根据学生ID查询预约
    public List<Appointment> selectByStudentId(Integer studentId) {
        return appointmentMapper.selectByStudentId(studentId);
    }

    // 查询可用的预约时间段
    public List<Appointment> selectAvailableSlots(Integer courseId) {
        return appointmentMapper.selectAvailableSlots(courseId);
    }

    // 新增预约时间段
    public void add(Appointment appointment) {
        // 验证时间是否合理
        if (appointment.getStartTime().isAfter(appointment.getEndTime())) {
            throw new CustomerException("500", "开始时间不能晚于结束时间");
        }
        appointment.setStatus("available");
        appointmentMapper.insert(appointment);
    }

    // 预约时间段
    public void bookSlot(Integer id, Integer studentId) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new CustomerException("500", "预约时间段不存在");
        }
        if (!"available".equals(appointment.getStatus())) {
            throw new CustomerException("500", "该时间段已被预约");
        }
        appointmentMapper.updateStatus(id, studentId, "booked");
    }

    // 取消预约
    public void cancelBooking(Integer id, Account account) {
        Appointment appointment = appointmentMapper.selectById(id);
        if (appointment == null) {
            throw new CustomerException("500", "预约时间段不存在");
        }

        // 时间验证（可选）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime appointmentTime = LocalDateTime.of(appointment.getDate(), appointment.getStartTime());
        if (appointmentTime.isBefore(now)) {
            throw new CustomerException("400", "无法取消已过期的预约");
        }

        // 老师：取消自己未被预约的时间段
        if ("TEA".equals(account.getRole())) {
            if (!account.getId().equals(appointment.getTeacherId())) {
                throw new CustomerException("403", "无权取消他人发布的时间段");
            }
            if ("available".equals(appointment.getStatus())) {
                appointmentMapper.deleteById(id);
            } else {
                throw new CustomerException("403", "该时间段已被预约，无法取消");
            }
            return;
        }

        // 学生：取消自己预约的时间段
        if ("STU".equals(account.getRole())) {
            if (!account.getId().equals(appointment.getStudentId())) {
                throw new CustomerException("403", "无权取消他人的预约");
            }
            if ("booked".equals(appointment.getStatus())) {
                appointmentMapper.updateStatus(id, null, "available");
            } else {
                throw new CustomerException("403", "该预约不可取消");
            }
            return;
        }

        // 非法角色
        throw new CustomerException("403", "无权限取消预约");
    }


    // 删除预约时间段
    public void deleteById(Integer id) {
        appointmentMapper.deleteById(id);
    }

    // 批量删除预约时间段
    public void deleteByCourseId(Integer courseId) {
        appointmentMapper.deleteByCourseId(courseId);
    }

    // 分页查询
    public PageInfo<Appointment> selectPage(Appointment appointment, Integer pageNum, Integer pageSize) {
        PageHelper.startPage( pageNum, pageSize);
        List<Appointment> list = appointmentMapper.selectAll(appointment);
        return PageInfo.of(list);
    }

    public int getTotalAppointments(Integer teacherId) {
        return appointmentMapper.getTotalAppointments(teacherId);
    }

    public List<Map<String, Object>> getAppointmentDistribution(Integer teacherId, String timeRange) {
        return appointmentMapper.getAppointmentDistribution(teacherId, timeRange);
    }
}