package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Appointment;
import com.example.springboot.entity.Article;
import com.example.springboot.entity.Course;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.AppointmentMapper;
import com.example.springboot.mapper.CourseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

        @Resource
        private CourseMapper courseMapper;

    @Resource
    private AppointmentMapper appointmentMapper;

    public void add(Course course) {
        // 设置创建时间为当前时间
        Date date = DateUtil.date();
        course.setCreateTime(date);

        // 检查 startTime 和 endTime 是否已经设定
        if (course.getStartTime() != null && course.getEndTime() != null) {
            // 计算持续时间，转换为毫秒，再转为小时数（你可以根据需要选择其他单位）
            long durationMillis = course.getEndTime().getTime() - course.getStartTime().getTime();

            // 转换为小时并设置到持续时间
            BigDecimal duration = BigDecimal.valueOf(durationMillis / (1000 * 60 * 60)); // 转为小时
            course.setDuration(duration);
        } else {
            // 提供处理 startTime 和 endTime 为空的情况
            throw new CustomerException("500","开始时间和结束时间不能为空");
        }

        // 插入课程信息到数据库
        courseMapper.insert(course);
    }


    public void update(Course course) {
        courseMapper.updateById(course);
    }

    public void deleteById(Integer id) {
        courseMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }
    public List<Course> selectAll (Course course) {
        List<Course> list = courseMapper.selectAll(course);
        return list;


    }

    public Course selectById(Integer id) {
            Course course =  courseMapper.selectById(id);
            return course;
    }

    public PageInfo<Course> selectPage(Course course, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectAll(course);
            return PageInfo.of(list);
    }

    public PageInfo<Course> selectPageByTeacherId(Integer teacherId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseMapper.selectByTeacherId(teacherId);
        return PageInfo.of(list);
    }

    public PageInfo<Course> selectPageByStudentId(Integer studentId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Appointment> appointments = appointmentMapper.selectByStudentId(studentId);


        // 从 appointments 中提取出所有的 courseId
        List<Integer> courseIds = appointments.stream()
                .map(Appointment::getCourseId)  // 假设 Appointment 里有 getCourseId 方法
                .distinct() // 确保课程 ID 的唯一性
                .collect(Collectors.toList());

        List<Course> list = courseMapper.selectByCourseIds(courseIds);;
        return PageInfo.of(list);
    }

    // 根据课程ID获取课程信息
    public Course getCourseTitleById(Integer courseId) {
        return courseMapper.getCourseTitleById(courseId);
    }

    public Integer selectCountByDate(String date) {
        return courseMapper.selectCountByDate(date);
    }

    public int countByTeacherId(Integer teacherId) {
        return courseMapper.countByTeacherId(teacherId);
    }

    public int getAllCourseCount() {
        return courseMapper.getAllCourseCount();
    }

    public void addClickCount(Integer id) {
        Course course = courseMapper.selectById(id);
        if (course != null) {
            course.setClickCount(course.getClickCount() + 1);
            courseMapper.updateById(course);
        }
    }

    public List<Course> getTopCourses() {
        return courseMapper.selectTopCourses();
    }
}
