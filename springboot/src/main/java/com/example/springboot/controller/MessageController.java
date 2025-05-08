package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Message;
import com.example.springboot.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @PostMapping("/send")
    public Result sendMessage(@RequestBody Message message) {
        messageService.saveMessage(message);
        return Result.success();
    }

    @GetMapping("/history")
    public Result getMessageHistory(@RequestParam Integer userNo,
                                  @RequestParam Integer friendNo,
                                  @RequestParam(defaultValue = "50") Integer limit) {
        List<Message> messages = messageService.getMessageHistory(userNo, friendNo, limit);
        return Result.success(messages);
    }

    @PostMapping("/read")
    public Result markAsRead(@RequestParam Integer userNo,
                           @RequestParam Integer friendNo) {
        messageService.markMessagesAsRead(userNo, friendNo);
        return Result.success();
    }

    @GetMapping("/unread/count")
    public Result getUnreadCount(@RequestParam Integer userNo) {
        Integer count = messageService.getUnreadCount(userNo);
        return Result.success(count);
    }
}