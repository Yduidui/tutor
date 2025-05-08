package com.example.springboot.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.springboot.entity.PageViewLog;
import com.example.springboot.mapper.PageViewMapper;
import com.example.springboot.mapper.PageViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class VisitLogScheduler {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private PageViewMapper pageViewMapper;

    @Scheduled(fixedRate = 5 * 60 * 1000) // 每 5 分钟执行一次
    public void syncLogsToDb() {
        List<PageViewLog> logs = new ArrayList<>();
        while (true) {
            String json = redisTemplate.opsForList().rightPop("visit:log:list");
            if (json == null) break;

            JSONObject obj = JSON.parseObject(json);
            PageViewLog log = new PageViewLog();
            log.setUserNo(Integer.valueOf(obj.getString("userNo")));
            log.setPath(obj.getString("path"));
            log.setIp(obj.getString("ip"));
            log.setUserAgent(obj.getString("userAgent"));
            log.setVisitTime(LocalDateTime.parse(obj.getString("visitTime")));
            logs.add(log);
        }

        if (!logs.isEmpty()) {
            pageViewMapper.insertBatch(logs);
        }
    }
}
