package com.example.springboot.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import com.example.springboot.service.TeacherService;
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
@RequestMapping("/teacher")
public class TeacherController {
    @Resource
    private TeacherService teacherService;

    /**
     * 新增教师
     */
    @PostMapping("/add")
    public Result add(@RequestBody Teacher teacher) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        teacher.setRegistrationTime(date);
        teacherService.add(teacher);
        return Result.success();
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Teacher teacher){  //将前端的json映射为Java的对象
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        teacher.setRegistrationTime(date);
        teacherService.register(teacher);
        return Result.success();
    }

    /**
     * 更新教师信息
     */
    @PutMapping("/update")
    public Result update(@RequestBody Teacher teacher) {
        teacherService.update(teacher);
        return Result.success(teacher);
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        teacherService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除教师
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        teacherService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询所有教师
     */
    @GetMapping("/selectAll")
    public Result selectAll(Teacher teacher) {
        List<Teacher> list = teacherService.selectAll(teacher);
        return Result.success(list);
    }

    /**
     * 查询单个教师
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Teacher teacher = teacherService.selectById(id);
        return Result.success(teacher);
    }


    @GetMapping("/search")
    public Result searchTeachers(@RequestParam String keyword) {
        List<Teacher> list = teacherService.searchTeachers(keyword);
        return Result.success(list);
    }


    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Teacher teacher,
                           @RequestParam(defaultValue = "1") Integer pageNum,
                           @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Teacher> pageInfo = teacherService.selectPage(teacher, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @GetMapping("/top")
    public Result getTopTeachers() {
        List<Teacher> topTeachers = teacherService.getTopTeachers();
        return Result.success(topTeachers);
    }

    /**
     * 导出excel
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 获取所有教师数据
        List<Teacher> teacherList = teacherService.selectAll(null);

        // 2. 创建 ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 3. 设置中文表头
        writer.addHeaderAlias("username", "账号");
        writer.addHeaderAlias("name", "姓名");
        writer.addHeaderAlias("sex", "性别");
        writer.addHeaderAlias("phone", "手机号");
        writer.addHeaderAlias("email", "邮箱");
        writer.addHeaderAlias("school", "学校");
        writer.addHeaderAlias("subject", "教授科目");
        writer.addHeaderAlias("totalClasses", "教学课程总数");
        writer.addHeaderAlias("totalSubjects", "教授科目数");
        writer.addHeaderAlias("totalHours", "累计授课小时数");
        writer.addHeaderAlias("registrationTime", "注册时间");

        // 只写出添加了别名的字段
        writer.setOnlyAlias(true);

        // 4. 写出数据
        writer.write(teacherList, true);

        // 5. 设置输出文件名称以及输出流的头信息
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String fileName = URLEncoder.encode("教师信息", "UTF-8");
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
        reader.addHeaderAlias("教授科目", "subject");
        reader.addHeaderAlias("教学课程总数", "totalClasses");
        reader.addHeaderAlias("教授科目数", "totalSubjects");
        reader.addHeaderAlias("累计授课小时数", "totalHours");
        reader.addHeaderAlias("注册时间", "registrationTime");

        // 读取 Excel 中的数据并转换成 Teacher 对象
        List<Teacher> teacherList = reader.readAll(Teacher.class);
        System.out.println("读取到的数据条数：" + teacherList.size());

        // 遍历每一行数据，并将教师信息保存到数据库
        for (Teacher teacher : teacherList) {
            try {
                teacherService.add(teacher);
                System.out.println("✅ 插入成功：" + teacher.getName());
            } catch (Exception e) {
                System.out.println("❌ 插入失败：" + teacher.getName() + "，原因：" + e.getMessage());
                e.printStackTrace();
            }
        }

        return Result.success();
    }
} 