package com.example.springboot.service;

import cn.hutool.core.date.DateUtil;
import com.example.springboot.entity.Subject;
import com.example.springboot.mapper.SubjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubjectService {

    @Resource
    private SubjectMapper subjectMapper;

    public void add(Subject subject) {
        Date date = DateUtil.date();
        subject.setCreatedAt(date);
        subject.setUpdatedAt(date);
        subjectMapper.insert(subject);
    }

    public void update(Subject subject) {
        subject.setUpdatedAt(DateUtil.date()); // 更新日期
        subjectMapper.updateById(subject);
    }

    public void deleteById(Integer id) {
        subjectMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public List<Subject> selectAll(Subject subject) {
        List<Subject> list = subjectMapper.selectAll(subject);
        return list;
    }

    public Subject selectById(Integer id) {
        return subjectMapper.selectById(id);
    }

    public PageInfo<Subject> selectPage(Subject subject, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Subject> list = subjectMapper.selectAll(subject);
        return new PageInfo<>(list);
    }
}