package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.Order;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.StudentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Map;

@Service
public class StudentService {

        @Resource
        private StudentMapper studentMapper;


    public void add(Student student) {
        // 检查用户名是否重复
        if (studentMapper.selectByUsername(student.getUsername()) != null) {
            throw new CustomerException("500", "账号重复，无法注册");
        }

        // 设置默认密码
        if (StrUtil.isBlank(student.getPassword())) {
            student.setPassword("123");
        }

        // 使用 BCrypt 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encryptedPwd = encoder.encode(student.getPassword());
        student.setPassword(encryptedPwd);  // 保存加密密码

        // 设置默认姓名（如果没填）
        if (StrUtil.isBlank(student.getName())) {
            student.setName(student.getUsername());
        }

        // 设置默认角色为学生
        student.setRole("STU");
        student.setRoleNo(1);

        // 插入数据库
        studentMapper.insert(student);

        // 设置 userNo：拼接 1 开头 + 补零 7 位数字
        String paddedId = String.format("%07d", student.getId());
        Integer userNo = Integer.parseInt("1" + paddedId);
        student.setUserNo(userNo);

        studentMapper.updateById(student);
    }

    public void update(Student student) {
        studentMapper.updateById(student);
    }

    public void deleteById(Integer id) {
        studentMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }
    public List<Student> selectAll (Student student) {
        List<Student> list = studentMapper.selectAll(student);
        return list;


    }

    public Student selectById(Integer id) {
            Student student =  studentMapper.selectById(id);
            return student;
    }

    public PageInfo<Student> selectPage(Student student, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Student> list = studentMapper.selectAll(student);
            return PageInfo.of(list);
    }


    public Student login(Account account) {
        String username = account.getUsername();
        String rawPassword = account.getPassword(); // 用户输入的密码

        Student dbStudent = studentMapper.selectByUsername(username);
        if (dbStudent == null) {
            throw new CustomerException("500", "账号不存在");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String dbPassword = dbStudent.getPassword();

        // 判断是否是 BCrypt 加密格式
        if (dbPassword != null && (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$"))) {
            // BCrypt 验证
            if (!encoder.matches(rawPassword, dbPassword)) {
                throw new CustomerException("500", "账号或密码错误");
            }
        } else {
            // 明文密码判断
            if (!rawPassword.equals(dbPassword)) {
                throw new CustomerException("500", "账号或密码错误");
            }
            // 自动升级为加密密码
            String encryptedPwd = encoder.encode(rawPassword);
            dbStudent.setPassword(encryptedPwd);
            studentMapper.updateById(dbStudent);
        }

        // 验证角色
        if (!"STU".equals(dbStudent.getRole())) {
            throw new CustomerException("500", "身份错误，无法以学生身份登录");
        }

        return dbStudent;
    }


    public void updatePassword(Account account) {
        System.out.println("Received Account: " + account);
        System.out.println("Received Account Password: " + account.getPassword());
        System.out.println("Received New Password: " + account.getNewPassword());

        Integer id = account.getId();
        Student student = studentMapper.selectById(id);
        System.out.println("Stored student password: " + student.getPassword());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 用 BCrypt 校验原密码
        if (!encoder.matches(account.getPassword(), student.getPassword())) {
            throw new CustomerException("500", "原密码错误");
        }

        // 加密新密码
        String encodedNewPassword = encoder.encode(account.getNewPassword());
        student.setPassword(encodedNewPassword);

        // 调用更新方法
        this.update(student);
    }
    public void register(Student student) {
        this.add(student);
    }

    public List<Student> searchStudents(String keyword) {
        return studentMapper.searchStudents(keyword);
    }

    public int getAllStudentCount() {
        return studentMapper.getAllStudentCount();
    }

    public List<Map<String, Object>> getTrendDataAsMap(String timeRange) {
        return studentMapper.getTrendGroupedByDate(timeRange);
    }

    public List<Student> getRecentStudents(int limit) {
        return studentMapper.getRecentStudents(limit);
    }
}
