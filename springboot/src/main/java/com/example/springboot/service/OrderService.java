package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Order;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.CourseMapper;
import com.example.springboot.mapper.OrderMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class OrderService {

    @Resource
    private OrderMapper orderMapper;
    @Resource
    private CourseMapper courseMapper;

    public void add(Order order) {
        // 设置创建时间为当前时间
        Date date = new Date();
        order.setOrderDate(date);

        // 生成随机的定长 orderNo
        int orderNoLength = 8; // 定义 orderNo 的长度
        String orderNo = generateOrderNo(orderNoLength);

        // 检查订单号是否重复
        while (orderMapper.selectByOrderNo(orderNo) != null) {
            orderNo = generateOrderNo(orderNoLength);
        }
        order.setOrderNo(orderNo); // 设置生成的 orderNo

        // 插入订单
        orderMapper.insert(order);

        // ★★★ 新增逻辑：更新课程的 student_id 字段 ★★★
        courseMapper.updateStudentIdByCourseId(order.getCourseId(), order.getStudentId());
    }

    /*
     * 生成指定长度的随机数字字符串
     */
    private String generateOrderNo(int length) {
        Random random = new Random();
        StringBuilder orderNo = new StringBuilder(length);

        // 生成随机数字
        for (int i = 0; i < length; i++) {
            orderNo.append(random.nextInt(10)); // 生成0-9的随机数字
        }

        return orderNo.toString();
    }


    public void update(Order order) {
        orderMapper.updateById(order);
    }

    public void deleteById(Integer id) {
        orderMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }

    public List<Order> selectAll(Order order) {
        List<Order> list = orderMapper.selectAll(order);
        return list;


    }

    public Order selectById(Integer id) {
        Order order = orderMapper.selectById(id);
        return order;
    }

    public PageInfo<Order> selectPage(Order order, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> list = orderMapper.selectAll(order);
        return PageInfo.of(list);
    }

    public Integer selectCountByDate(String date) {
        return orderMapper.selectCountByDate(date);
    }

    public Order selectByOrderNo(String orderNo) {

        return orderMapper.selectByOrderNo(orderNo);
    }

    public Order selectByTeacherId(Integer teacherId) {
        Order order = orderMapper.selectByTeacherId(teacherId);
        return order;
    }

    public Order selectByAppointmentAndStudent(Integer appointmentId, Integer studentId) {
        return orderMapper.selectByAppointmentAndStudent(appointmentId, studentId);
    }

    public int getAllOrderCount() {
        return orderMapper.getAllOrderCount();
    }

    public List<Map<String, Object>> getTrendDataAsMap(String timeRange) {
        return orderMapper.getTrendGroupedByDate(timeRange);
    }

    public List<Order> getRecentOrders(int limit) {
        return orderMapper.getRecentOrders(limit);
    }
}
