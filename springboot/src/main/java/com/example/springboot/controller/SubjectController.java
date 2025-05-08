package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Subject;
import com.example.springboot.service.SubjectService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
public class SubjectController {

    @Resource
    private SubjectService subjectService;

    /**
     * 新增科目
     */
    @PostMapping("/add")
    public Result add(@RequestBody Subject subject) {
        subjectService.add(subject);
        return Result.success();
    }

    /**
     * 更新科目
     */
    @PutMapping("/update")
    public Result update(@RequestBody Subject subject) {
        subjectService.update(subject);
        return Result.success(subject);
    }

    /**
     * 删除科目
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        subjectService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除科目
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        subjectService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 查询所有科目
     */
    @GetMapping("/selectAll")
    public Result selectAll(Subject subject) {
        List<Subject> list = subjectService.selectAll(subject);
        return Result.success(list);
    }

    /**
     * 查询单个科目
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Subject subject = subjectService.selectById(id);
        return Result.success(subject);
    }

    /**
     * 分页查询科目
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Subject subject,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Subject> pageInfo = subjectService.selectPage(subject, pageNum, pageSize);
        return Result.success(pageInfo);
    }
}