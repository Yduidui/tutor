package com.example.springboot.service;

import com.example.springboot.entity.Friendship;
import com.example.springboot.mapper.FriendshipMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {
    @Resource
    private FriendshipMapper friendshipMapper;

    public void addFriend(Friendship friendship) {
        friendship.setStatus("PENDING");
        friendshipMapper.insert(friendship);
    }

    public void acceptFriendRequest(Integer id) {
        // 查出原始好友请求记录
        Friendship request = friendshipMapper.selectById(id);
        if (request == null || !"PENDING".equals(request.getStatus())) {
            throw new RuntimeException("无效或已处理的好友请求");
        }

        // 更新原始请求状态为 ACCEPTED
        friendshipMapper.updateStatus(id, "ACCEPTED");

        // 查找反向记录并更新状态（前端已创建，此处无需插入）
        Friendship reverse = friendshipMapper.selectByUserAndFriend(
                request.getFriendNo(), request.getUserNo());

        if (reverse != null && !"ACCEPTED".equals(reverse.getStatus())) {
            friendshipMapper.updateStatus(reverse.getId(), "ACCEPTED");
        }
    }


    public void rejectFriendRequest(Integer id) {
        friendshipMapper.updateStatus(id, "REJECTED");
    }

    public List<Friendship> getFriends(Integer userNo) {
        return friendshipMapper.selectByUserNo(userNo);
    }

    public List<Friendship> getPendingRequests(Integer userNo) {
        return friendshipMapper.selectPendingRequests(userNo);
    }

    public void deleteByUserAndFriend(Integer userNo, Integer friendNo) {
        // 删除用户与好友的关系
        friendshipMapper.deleteByUserAndFriend(userNo, friendNo);
        // 删除好友与用户的关系（双向删除）
        friendshipMapper.deleteByUserAndFriend(friendNo, userNo);
    }


    public Friendship getFriendship(Integer userNo, Integer friendNo) {
        return friendshipMapper.selectByUserAndFriend(userNo, friendNo);
    }
} 