package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Article;
import com.example.springboot.service.ArticleService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Article article){  //将前端的json映射为Java的对象
        articleService.add(article);
        return Result.success();
    }

    /**
    * 更新数据
    */
    @PutMapping("/update")
    public Result update(@RequestBody Article article){
        articleService.update(article);
        return Result.success(article);
    }

    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        articleService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        articleService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Article article){
        List<Article> list = articleService.selectAll(article);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Article article = articleService.selectById(id);
        return Result.success(article);
    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Article article,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Article> pageInfo = articleService.selectPage(article,pageNum, pageSize);
        return Result.success(pageInfo);

    }

//    /**
//     *
//     * url传参
//     */
//    @GetMapping("/selectOne")
//    public Result selectOne(@RequestParam Integer id, @RequestParam String name){
//        Employee article = articleService.selectById(id);
//        return Result.success(article);
//    }
}
