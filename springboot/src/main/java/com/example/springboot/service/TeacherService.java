package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.Student;
import com.example.springboot.entity.Teacher;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.TeacherMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.List;
import java.util.Map;

@Service
public class TeacherService {

    @Resource
    private TeacherMapper teacherMapper;



    public void add(Teacher teacher) {
        if (teacherMapper.selectByUsername(teacher.getUsername()) != null) {
            throw new CustomerException("500", "账号重复，无法注册");
        }

        // 设置默认密码
        if (StrUtil.isBlank(teacher.getPassword())) {
            teacher.setPassword("123456");
        }

        // 加密密码
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        teacher.setPassword(encoder.encode(teacher.getPassword()));

        // 设置默认姓名
        if (StrUtil.isBlank(teacher.getName())) {
            teacher.setName(teacher.getUsername());
        }

        teacher.setRole("TEA");
        teacher.setRoleNo(1);

        teacherMapper.insert(teacher);

        // 构造 userNo
        String paddedId = String.format("%07d", teacher.getId());
        Integer userNo = Integer.parseInt("2" + paddedId);
        teacher.setUserNo(userNo);

        teacherMapper.updateById(teacher);
    }

    public void update(Teacher teacher) {
        teacherMapper.updateById(teacher);
    }

    public void deleteById(Integer id) {
        teacherMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public List<Teacher> selectAll(Teacher teacher) {
        List<Teacher> list = teacherMapper.selectAll(teacher);
        return list;
    }

    public Teacher selectById(Integer id) {
        Teacher teacher = teacherMapper.selectById(id);
        return teacher;
    }

    public PageInfo<Teacher> selectPage(Teacher teacher, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Teacher> list = teacherMapper.selectAll(teacher);
        return PageInfo.of(list);
    }

    public Teacher login(Account account) {
        String username = account.getUsername();
        String rawPassword = account.getPassword(); // 用户输入的密码

        Teacher dbTeacher = teacherMapper.selectByUsername(username);
        if (dbTeacher == null) {
            throw new CustomerException("500", "账号不存在");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String dbPassword = dbTeacher.getPassword();

        // 判断密码是否是加密格式
        if (dbPassword != null && (dbPassword.startsWith("$2a$") || dbPassword.startsWith("$2b$") || dbPassword.startsWith("$2y$"))) {
            // 使用加密格式验证
            if (!encoder.matches(rawPassword, dbPassword)) {
                throw new CustomerException("500", "账号或密码错误");
            }
        } else {
            // 明文密码校验
            if (!rawPassword.equals(dbPassword)) {
                throw new CustomerException("500", "账号或密码错误");
            }
            // 自动升级为加密密码
            String encryptedPwd = encoder.encode(rawPassword);
            dbTeacher.setPassword(encryptedPwd);
            teacherMapper.updateById(dbTeacher);
        }

        return dbTeacher;
    }



    public void updatePassword(Account account) {
        Integer id = account.getId();
        Teacher teacher = teacherMapper.selectById(id);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(account.getPassword(), teacher.getPassword())) {
            throw new CustomerException("500", "原密码错误");
        }

        // 加密新密码
        teacher.setPassword(encoder.encode(account.getNewPassword()));

        this.update(teacher);
    }


    public void register(Teacher teacher) {
        this.add(teacher);
    }

    public List<Teacher> searchTeachers(String keyword) {
        return teacherMapper.searchTeachers(keyword);
    }

    public int getAllTeacherCount() {
        return teacherMapper.getAllTeacherCount();
    }

    public List<Map<String, Object>> getTrendDataAsMap(String timeRange) {
        return teacherMapper.getTrendGroupedByDate(timeRange);
    }

    public List<Teacher> getRecentTeachers(int limit) {
        return teacherMapper.getRecentTeachers(limit);
    }

    public List<Teacher> getTopTeachers() {
        return teacherMapper.selectTopTeachers();
    }
}