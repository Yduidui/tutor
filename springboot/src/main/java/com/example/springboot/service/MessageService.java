package com.example.springboot.service;

import com.example.springboot.entity.Message;
import com.example.springboot.mapper.MessageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageService {

    @Resource
    private MessageMapper messageMapper;

    public void saveMessage(Message message) {
        message.setCreateTime(new Date());
        message.setStatus(0); // 默认未读
        messageMapper.insert(message);
    }

    public List<Message> getMessageHistory(Integer userNo, Integer friendNo, Integer limit) {
        return messageMapper.selectHistory(userNo, friendNo, limit);
    }

    public void markMessagesAsRead(Integer userNo, Integer friendNo) {
        messageMapper.updateStatus(userNo, friendNo);
    }

    public Integer getUnreadCount(Integer userNo) {
        return messageMapper.selectUnreadCount(userNo);
    }
}