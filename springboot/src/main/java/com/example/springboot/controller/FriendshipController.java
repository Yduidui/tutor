package com.example.springboot.controller;

import com.example.springboot.common.Result;
import com.example.springboot.entity.Friendship;
import com.example.springboot.service.FriendshipService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {
    @Resource
    private FriendshipService friendshipService;

    // 添加好友
    @PostMapping("/add")
    public Result addFriend(@RequestBody Friendship friendship) {
        friendshipService.addFriend(friendship);
        return Result.success();
    }

    // 接受好友请求
    @PostMapping("/accept/{id}")
    public Result acceptFriendRequest(@PathVariable Integer id) {
        friendshipService.acceptFriendRequest(id);
        return Result.success();
    }

    // 拒绝好友请求
    @PostMapping("/reject/{id}")
    public Result rejectFriendRequest(@PathVariable Integer id) {
        friendshipService.rejectFriendRequest(id);
        return Result.success();
    }

    // 获取好友列表
    @GetMapping("/list")
    public Result getFriends(@RequestParam Integer userNo) {
        List<Friendship> friends = friendshipService.getFriends(userNo);
        return Result.success(friends);
    }

    // 获取待处理好友请求
    @GetMapping("/pending")
    public Result getPendingRequests(@RequestParam Integer userNo) {
        List<Friendship> pendingRequests = friendshipService.getPendingRequests(userNo);
        return Result.success(pendingRequests);
    }

    @DeleteMapping("/deleteByUserAndFriend")
    public Result deleteFriendByUserAndFriend(@RequestParam Integer userNo, @RequestParam Integer friendNo) {
        friendshipService.deleteByUserAndFriend(userNo, friendNo);
        return Result.success();
    }


    // 检查好友关系
    @GetMapping("/check")
    public Result checkFriendship(@RequestParam Integer userNo, @RequestParam Integer friendNo) {
        Friendship friendship = friendshipService.getFriendship(userNo, friendNo);
        return Result.success(friendship);
    }
}