package com.example.springboot.service;

import com.example.springboot.entity.Account;
import com.example.springboot.entity.PageViewLog;
import com.example.springboot.entity.PageViewLog;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.PageViewMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class PageViewService {
    @Resource
    private PageViewMapper pageViewMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;

//    private static final String ONLINE_USERS_KEY = "online:users";
//    private static final String PAGE_VIEWS_KEY = "page:views:";
//    private static final String DAILY_VIEWS_KEY = "daily:views:";

    // 记录页面访问
//    public void recordPageView(String pageUrl, String ipAddress, String userAgent) {
//        PageViewLog log = new PageViewLog();
//        log.setPageUrl(pageUrl);
//        log.setIpAddress(ipAddress);
//        log.setViewTime(LocalDateTime.now());
//        log.setUserAgent(userAgent);
//        pageViewMapper.insert(log);
//        
//        // 更新Redis中的访问统计
//        String today = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
//        redisTemplate.opsForValue().increment(DAILY_VIEWS_KEY + today);
//    }

    public void update(PageViewLog pageViewLog) {
        pageViewMapper.updateById(pageViewLog);
    }

    public void deleteById(Integer id) {
        pageViewMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }
    public List<PageViewLog> selectAll (PageViewLog pageViewLog) {
        List<PageViewLog> list = pageViewMapper.selectAll(pageViewLog);
        return list;


    }

    public PageViewLog selectById(Integer id) {
        PageViewLog pageViewLog =  pageViewMapper.selectById(id);
        return pageViewLog;
    }

    public PageInfo<PageViewLog> selectPage(PageViewLog pageViewLog, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PageViewLog> list = pageViewMapper.selectAll(pageViewLog);
        return PageInfo.of(list);
    }

    public List<Map<String, Object>> getTrendDataAsMap(String timeRange) {
        return pageViewMapper.getTrendGroupedByDate(timeRange);
    }

    public int getAllVisitCount() {
        return pageViewMapper.getAllVisitCount();
    }

    public List<Map<String, Object>> getThisWeekVisitData() {
        List<Map<String, Object>> result = new ArrayList<>();

        // 获取当前周的星期一作为起始日期
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(DayOfWeek.MONDAY);

        for (int i = 0; i < 7; i++) {
            LocalDate day = monday.plusDays(i);
            int count = pageViewMapper.getVisitCountByDate(day); // 你需要添加这个方法
            Map<String, Object> map = new HashMap<>();
            map.put("day", day.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.CHINESE)); // 输出：星期一
            map.put("count", count);
            result.add(map);
        }

        return result;
    }
}