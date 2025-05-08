package com.example.springboot.mapper;

import com.example.springboot.entity.Friendship;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendshipMapper {

    void insert(Friendship friendship);


    void updateStatus(@Param("id") Integer id, @Param("status") String status);


    List<Friendship> selectByUserNo(@Param("userNo") Integer userNo);
    Friendship selectById(Integer id);

    Friendship selectByUserAndFriend(@Param("userNo") Integer userNo, @Param("friendNo") Integer friendNo);

    void deleteByUserAndFriend(@Param("userNo") Integer userNo, @Param("friendNo") Integer friendNo);

    List<Friendship> selectPendingRequests(@Param("userNo") Integer userNo);

    void deleteById(@Param("id") Integer id);
} 