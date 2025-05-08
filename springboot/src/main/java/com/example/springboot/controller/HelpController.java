package com.example.springboot.controller;

import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.springboot.common.Result;
import com.example.springboot.entity.Help;
import com.example.springboot.service.HelpService;
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
@RequestMapping("/help")
public class HelpController {
    @Resource
    private HelpService helpService;

    /**
     * 新增数据
     *
     */
    @PostMapping("/add")
    public Result add(@RequestBody Help help){  //将前端的json映射为Java的对象
        LocalDateTime localDateTime = LocalDateTime.now();
        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        help.setCreatedAt(date);
        helpService.add(help);
        return Result.success();
    }
    
    /**
    * 更新数据
    */
    @PutMapping("/update")
    public Result update(@RequestBody Help help){
        helpService.update(help);
        return Result.success(help);
    }

    @PostMapping("/reply")
    public Result replyHelp(@RequestBody Help help) {
        help.setRepliedAt(new Date()); // 服务器当前时间
        helpService.replyHelp(help);
        return Result.success();
    }



    /**
     * 删除数据
     */
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id){
        helpService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除数据
     */
    @DeleteMapping("/deleteBatch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        helpService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Help help){
        List<Help> list = helpService.selectAll(help);
        return Result.success(list);
    }

    /**
     * 查询单个
     * @param id
     * @return
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id){
        Help help = helpService.selectById(id);
        return Result.success(help);
    }

    @GetMapping("/search")
    public Result searchHelps(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) Integer priority,
                            @RequestParam(required = false) Integer status) {
        List<Help> list = helpService.searchHelps(keyword, priority, status);
        return Result.success(list);
    }
    /**
     * 分页查询
     * pageNum 当前页码
     * pageSize 每页个数
     */
    @GetMapping("/selectPage")
    public Result selectPage(Help help,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<Help> pageInfo = helpService.selectPage(help,pageNum, pageSize);
        return Result.success(pageInfo);

    }


    /**
     * 导出excel
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) throws IOException {
        // 1. 获取所有学生数据
        List<Help> helpList = helpService.selectAll(null);

        // 2. 创建 ExcelWriter
        ExcelWriter writer = ExcelUtil.getWriter(true);

        // 3. 设置中文表头
        // 3. 设置中文表头
        writer.addHeaderAlias("userNo", "用户编号");
        writer.addHeaderAlias("title", "工单标题");
        writer.addHeaderAlias("content", "问题描述");
        writer.addHeaderAlias("status", "状态");
        writer.addHeaderAlias("priority", "优先级");
        writer.addHeaderAlias("createdAt", "创建时间");
        writer.addHeaderAlias("reply", "回复内容");
        writer.addHeaderAlias("repliedAt", "回复时间");
        writer.addHeaderAlias("repliedBy", "指派管理员");

        // 只写出添加了别名的字段
        writer.setOnlyAlias(true);

        // 4. 写出数据
        writer.write(helpList, true);

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

        // 设置Excel表头映射：Excel列名 <> 实体类字段名
        reader.addHeaderAlias("用户编号", "userNo");
        reader.addHeaderAlias("工单标题", "title");
        reader.addHeaderAlias("问题描述", "content");
        reader.addHeaderAlias("状态", "status");
        reader.addHeaderAlias("优先级", "priority");
        reader.addHeaderAlias("创建时间", "createdAt");
        reader.addHeaderAlias("回复内容", "reply");
        reader.addHeaderAlias("回复时间", "repliedAt");
        reader.addHeaderAlias("指派管理员", "repliedBy");

        // 读取 Excel 中的数据并转换成 Help 对象
        List<Help> helpList = reader.readAll(Help.class);
        System.out.println("读取到的数据条数：" + helpList.size());

        // 遍历每一行数据，并将学生信息保存到数据库
        for (Help help : helpList) {
            try {
                helpService.add(help);  // 保存学生数据
                System.out.println("✅ 插入成功：" + help.getTitle());
            } catch (Exception e) {
                System.out.println("❌ 插入失败：" + help.getTitle() + "，原因：" + e.getMessage());
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
//        Help help = helpService.selectById(id);
//        return Result.success(help);
//    }
}
