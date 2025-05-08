package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Comment;
import com.example.springboot.entity.Comment;
import com.example.springboot.service.CommentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment){  //将前端的json映射为Java的对象
        commentService.add(comment);
        return Result.success();
    }

    /**
     * 更新数据
     */
    @PutMapping("/update")
    public Result update(@RequestBody Comment comment){
        commentService.update(comment);
        return Result.success(comment);
    }

    @PutMapping("/updateIsDeleted/{id}")
    public Result updateIsDeleted(@PathVariable Integer id) {
        Comment comment = commentService.selectById(id);
        // 切换 0/1 状态
        Integer current = comment.getIsDeleted();
        comment.setIsDeleted(current != null && current == 1 ? 0 : 1);
        commentService.update(comment);
        return Result.success(comment); // 可选：返回更新后的对象
    }


    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        commentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        commentService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Comment comment){
        List<Comment> list = commentService.selectAll(null, comment);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Comment comment = commentService.selectById(id);
        return Result.success(comment);
    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Comment comment,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             @RequestParam(required = false) String keyword) {
        PageInfo<Comment> pageInfo = commentService.selectPage(keyword, comment, pageNum, pageSize);
        return Result.success(pageInfo);
    }

    @PostMapping("/submit")
    public Result submitComment(@RequestBody Comment comment) {
        commentService.submitComment(comment);
        return Result.success();
    }

    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherComments(@PathVariable Integer teacherId) {
        List<Comment> comments = commentService.getTeacherComments(teacherId);
        return Result.success(comments);
    }

    @GetMapping("/student/{studentId}")
    public Result getStudentComments(@PathVariable Integer studentId) {
        List<Comment> comments = commentService.getStudentComments(studentId);
        return Result.success(comments);
    }

    @GetMapping("/average/{teacherId}")
    public Result getAverageRating(@PathVariable Integer teacherId) {
        Double average = commentService.getAverageRating(teacherId);
        return Result.success(average);
    }

    @PostMapping("/hide/{id}")
    public Result hideComment(@PathVariable Integer id) {
        commentService.softDeleteComment(id);
        return Result.success();
    }

    @PostMapping("/restore/{id}")
    public Result restoreComment(@PathVariable Integer id) {
        commentService.restoreComment(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        commentService.deleteComment(id);
        return Result.success();
    }

    @GetMapping("/distribution/{teacherId}")
    public Result getRatingDistribution(@PathVariable Integer teacherId) {
        List<Map<String, Object>> distribution = commentService.getRatingDistribution(teacherId);
        return Result.success(distribution);
    }

    @GetMapping("/trend/{teacherId}")
    public Result getCommentTrend(@PathVariable Integer teacherId) {
        List<Map<String, Object>> trend = commentService.getCommentTrend(teacherId);
        return Result.success(trend);
    }
}