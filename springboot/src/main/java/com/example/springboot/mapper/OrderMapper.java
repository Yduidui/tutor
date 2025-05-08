package com.example.springboot.mapper;


import com.example.springboot.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface OrderMapper {
    List<Order> selectAll(Order order);

//    @Select("select * from `order` where id = #{id}")  注解方式
    Order selectById(Integer id);

    void insert(Order order);

    void updateById(Order order);

    void deleteById(Integer id);

    @Select("select * from `order` where orderNo = #{orderNo}")
    Order selectByOrderNo(String orderNo);

    @Select("select count(*) from `order` where time like'%${date}%'")
    Integer selectCountByDate(String date);

    @Select("select * from `order` where teacher_id = #{teacherId}")
    Order selectByTeacherId(Integer teacherId);

    Order selectByAppointmentAndStudent(@Param("appointmentId") Integer appointmentId,
                                        @Param("studentId") Integer studentId);

    int getAllOrderCount();

    List<Map<String, Object>> getTrendGroupedByDate(@Param("timeRange") String timeRange);

    List<Order> getRecentOrders(@Param("limit") int limit);
}
