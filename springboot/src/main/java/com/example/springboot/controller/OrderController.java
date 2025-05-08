package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Order;
import com.example.springboot.service.OrderService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Resource
    private OrderService orderService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Order order){  //将前端的json映射为Java的对象
        orderService.add(order);
        return Result.success();
    }

    /**
    * 更新数据
    */
    @PutMapping("/update")
    public Result update(@RequestBody Order order){
        orderService.update(order);
        return Result.success(order);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        orderService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        orderService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Order order){
        List<Order> list = orderService.selectAll(order);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Order order = orderService.selectById(id);
        return Result.success(order);
    }

    /**
     * 查询单个
     * @param teacherId
     * @return
     */
    @GetMapping("/selectByTeacherId/{teacherId}")
    public Result selectByTeacherId(@PathVariable Integer teacherId){
        Order order = orderService.selectByTeacherId(teacherId);
        return Result.success(order);
    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Order order,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Order> pageInfo = orderService.selectPage(order,pageNum, pageSize);
        return Result.success(pageInfo);

    }


    @GetMapping("/selectByAppointment/{appointmentId}")
    public Result selectByAppointment(@PathVariable Integer appointmentId,
                                      @RequestParam Integer studentId) {
        Order order = orderService.selectByAppointmentAndStudent(appointmentId, studentId);
        return order != null ? Result.success(order) : Result.error("500","未找到对应订单");
    }

//    /**
//     *
//     * url传参
//     */
//    @GetMapping("/selectOne")
//    public Result selectOne(@RequestParam Integer id, @RequestParam String name){
//        Employee order = orderService.selectById(id);
//        return Result.success(order);
//    }
}
