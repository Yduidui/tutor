package com.example.springboot.service;

import cn.hutool.core.util.StrUtil;
import com.example.springboot.entity.Account;
import com.example.springboot.entity.Help;
import com.example.springboot.entity.News;
import com.example.springboot.exception.CustomerException;
import com.example.springboot.mapper.HelpMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class HelpService {

        @Resource
        private HelpMapper helpMapper;


    public void add(Help help) {
        // 插入数据库
        helpMapper.insert(help);
    }

    public void update(Help help) {
        helpMapper.updateById(help);
    }

    public void deleteById(Integer id) {
        helpMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }

    }
    public List<Help> selectAll (Help help) {
        List<Help> list = helpMapper.selectAll(help);
        return list;


    }

    public Help selectById(Integer id) {
            Help help =  helpMapper.selectById(id);
            return help;
    }

    public PageInfo<Help> selectPage(Help help, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Help> list = helpMapper.selectAll(help);
            return PageInfo.of(list);
    }


    public List<Help> searchHelps(String keyword, Integer priority, Integer status) {
        return helpMapper.searchHelps(keyword, priority, status);
    }

    public int getAllHelpCount() {
        return helpMapper.getAllHelpCount();
    }

    public List<Map<String, Object>> getTrendDataAsMap(String timeRange) {
        return helpMapper.getTrendGroupedByDate(timeRange);
    }


    public void replyHelp(Help help) {
        if (help.getId() == null) {
            throw new CustomerException("500","工单ID不能为空");
        }
        if (help.getReply() == null || help.getReply().trim().isEmpty()) {
            throw new CustomerException("500","回复内容不能为空");
        }
        help.setStatus(2); // 设置为已解决状态
        helpMapper.replyHelp(help);
    }

    public List<Help> getLatestHelps(int limit) {
        return helpMapper.getLatestHelps(limit);
    }
}
