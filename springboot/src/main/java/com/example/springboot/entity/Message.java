package com.example.springboot.entity;

import java.util.Date;


public class Message {
    private Integer id;
    private Integer fromUserNo;     //发送
    private Integer toUserNo;       //接收
    private String content;
    private Date createTime;
    private Integer status; // 0: 未读, 1: 已读

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromUserNo() {
        return fromUserNo;
    }

    public void setFromUserNo(Integer fromUserNo) {
        this.fromUserNo = fromUserNo;
    }

    public Integer getToUserNo() {
        return toUserNo;
    }

    public void setToUserNo(Integer toUserNo) {
        this.toUserNo = toUserNo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}