package com.example.springboot.mapper;

import com.example.springboot.entity.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {
    

    void insert(Message message);


    List<Message> selectHistory(@Param("userNo") Integer userNo, 
                              @Param("friendNo") Integer friendNo,
                              @Param("limit") Integer limit);


    void updateStatus(@Param("userNo") Integer userNo, @Param("friendNo") Integer friendNo);


    Integer selectUnreadCount(@Param("userNo") Integer userNo);
} 