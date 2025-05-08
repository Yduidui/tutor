package com.example.springboot.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot.common.Result;
import com.example.springboot.entity.PageViewLog;
import com.example.springboot.entity.PageViewLog;
import com.example.springboot.service.PageViewService;
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
import java.util.Map;

@RestController
@RequestMapping("/pageView")
public class PageViewController {
    @Resource
    private PageViewService pageViewService;

    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        pageViewService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        pageViewService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(PageViewLog pageViewLog){
        List<PageViewLog> list = pageViewService.selectAll(pageViewLog);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        PageViewLog pageViewLog = pageViewService.selectById(id);
        return Result.success(pageViewLog);
    }

    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(PageViewLog pageViewLog,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<PageViewLog> pageInfo = pageViewService.selectPage(pageViewLog,pageNum, pageSize);
        return Result.success(pageInfo);

    }


   

    
} 