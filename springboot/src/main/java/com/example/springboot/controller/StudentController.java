package com.example.springboot.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import com.example.springboot.service.StudentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Resource
    private StudentService studentService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Student student){  //将前端的json映射为Java的对象
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        student.setRegistrationTime(date);
        studentService.add(student);
        return Result.success();
    }
    /**
     * 注册
     *
     */
    @PostMapping("/register")
    public Result register(@RequestBody Student student){  //将前端的json映射为Java的对象
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        student.setRegistrationTime(date);
        studentService.register(student);
        return Result.success();
    }

    /**
    * 更新数据
    */
    @PutMapping("/update")
    public Result update(@RequestBody Student student){
        studentService.update(student);
        return Result.success(student);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        studentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        studentService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Student student){
        List<Student> list = studentService.selectAll(student);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Student student = studentService.selectById(id);
        return Result.success(student);
    }

    @GetMapping("/search")
    public Result searchStudents(@RequestParam String keyword) {
        List<Student> list = studentService.searchStudents(keyword);
        return Result.success(list);
    }
    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Student student,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Student> pageInfo = studentService.selectPage(student,pageNum, pageSize);
        return Result.success(pageInfo);

    }


    /**
     * 导出excel
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 获取所有学生数据
        List<Student> studentList = studentService.selectAll(null);

        // 2. 创建 ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 3. 设置中文表头
        writer.addHeaderAlias("username", "账号");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("sex", "性别");
        writer.addHeaderAlias("phone", "手机号");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("school", "学校");
        writer.addHeaderAlias("grade", "年级");
        writer.addHeaderAlias("parentName", "家长姓名");
        writer.addHeaderAlias("parentPhone", "家长电话");
        writer.addHeaderAlias("registrationTime", "注册时间");

        // 只写出添加了别名的字段
        writer.setOnlyAlias(true);

        // 4. 写出数据
        writer.write(studentList, true);

        // 5. 设置输出文件名称以及输出流的头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("学生信息", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

        // 6. 输出文件到响应流并关闭
        ServletOutputStream os = response.getOutputStream();
        writer.flush(os);
        writer.close();
    }

    /**
     * 导入excel
     */
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        // 读取 Excel 文件
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());

        // 设置 Excel 表头映射
        reader.addHeaderAlias("账号", "username");
        reader.addHeaderAlias("姓名", "name");
        reader.addHeaderAlias("性别", "sex");
        reader.addHeaderAlias("手机号", "phone");
        reader.addHeaderAlias("邮箱", "email");
        reader.addHeaderAlias("学校", "school");
        reader.addHeaderAlias("年级", "grade");
        reader.addHeaderAlias("家长姓名", "parentName");
        reader.addHeaderAlias("家长电话", "parentPhone");
        reader.addHeaderAlias("注册时间", "registrationTime");

        // 读取 Excel 中的数据并转换成 Student 对象
        List<Student> studentList = reader.readAll(Student.class);
        System.out.println("读取到的数据条数：" + studentList.size());

        // 遍历每一行数据，并将学生信息保存到数据库
        for (Student student : studentList) {
            try {
                studentService.add(student);  // 保存学生数据
                System.out.println("✅ 插入成功：" + student.getUsername());
            } catch (Exception e) {
                System.out.println("❌ 插入失败：" + student.getUsername() + "，原因：" + e.getMessage());
                e.printStackTrace();  // 打印具体异常
            }
        }

        return Result.success();
    }

//    /**
//     *
//     * url传参
//     */
//    @GetMapping("/selectOne")
//    public Result selectOne(@RequestParam Integer id, @RequestParam String name){
//        Student student = studentService.selectById(id);
//        return Result.success(student);
//    }
}
