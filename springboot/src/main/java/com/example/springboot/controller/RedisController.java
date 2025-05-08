package com.example.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.example.springboot.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/redis")
public class RedisController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 记录访问
    @PostMapping("/visit")
    public Result logVisit(@RequestParam String userNo, @RequestParam String path, HttpServletRequest request) {
        Map<String, Object> log = new HashMap<>();
        log.put("userNo", userNo);
        log.put("path", path);
        log.put("ip", request.getRemoteAddr());
        log.put("userAgent", request.getHeader("User-Agent"));
        log.put("visitTime", LocalDateTime.now().toString());

        // 1. 写入 Redis 列表（稍后入库）
        redisTemplate.opsForList().leftPush("visit:log:list", JSON.toJSONString(log));

        // 2. 增加总访问量
        redisTemplate.opsForValue().increment("visit:count");

        // 3. 增加今日访问量
        String todayKey = "visit:count:" + LocalDate.now();
        redisTemplate.opsForValue().increment(todayKey);

        return Result.success();
    }

    // 获取总访问量
    @GetMapping("/visit/total")
    public Result getTotalVisitCount() {
        String count = redisTemplate.opsForValue().get("visit:count");
        return Result.success(count == null ? 0 : Integer.parseInt(count));
    }

    // 获取今日访问量
    @GetMapping("/visit/today")
    public Result getTodayVisitCount() {
        String todayKey = "visit:count:" + LocalDate.now();
        String count = redisTemplate.opsForValue().get(todayKey);
        return Result.success(count == null ? 0 : Integer.parseInt(count));
    }
}
