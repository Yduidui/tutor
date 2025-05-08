package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.Teacher;
import com.example.springboot.service.CourseService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {
    @Resource
    private CourseService courseService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Course course){  //将前端的json映射为Java的对象
        courseService.add(course);
        return Result.success();
    }

    /**
    * 更新数据
    */
    @PutMapping("/update")
    public Result update(@RequestBody Course course){
        courseService.update(course);
        return Result.success(course);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        courseService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        courseService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Course course){
        List<Course> list = courseService.selectAll(course);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Course course = courseService.selectById(id);
        return Result.success(course);
    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Course course,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Course> pageInfo = courseService.selectPage(course,pageNum, pageSize);
        return Result.success(pageInfo);

    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPageByTeacherId")
    public Result selectPageByTeacherId(
                             @RequestParam Integer teacherId,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Course> pageInfo = courseService.selectPageByTeacherId(teacherId,pageNum, pageSize);
        return Result.success(pageInfo);

    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPageByStudentId")
    public Result selectPageByStudentId(
            @RequestParam Integer studentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Course> pageInfo = courseService.selectPageByStudentId(studentId,pageNum, pageSize);
        return Result.success(pageInfo);

    }
    @PutMapping("/addClickCount/{id}")
    public Result addClickCount(@PathVariable Integer id) {
        courseService.addClickCount(id);
        return Result.success();
    }

    @GetMapping("/top")
    public Result getTopCourses() {
        List<Course> topCourses = courseService.getTopCourses();
        return Result.success(topCourses);
    }
//    /**
//     *
//     * url传参
//     */
//    @GetMapping("/selectOne")
//    public Result selectOne(@RequestParam Integer id, @RequestParam String name){
//        Employee course = courseService.selectById(id);
//        return Result.success(course);
//    }
}
