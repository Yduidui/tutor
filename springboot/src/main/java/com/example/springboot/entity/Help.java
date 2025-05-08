package com.example.springboot.entity;

import java.util.Date;

public class Help {
    private Integer id; // 工单ID
    private Integer userNo; // 提交用户编号
    private String title; // 工单标题
    private String content; // 问题描述内容
    private Integer status; // 状态（0：新建，1：处理中，2：已解决）
    private Integer priority; // 优先级（0：低，1：中，2：高）
    private Date createdAt; // 创建时间
    private String reply; // 回复内容
    private Date repliedAt; // 回复时间
    private Integer repliedBy; // 指派管理员ID


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Date getRepliedAt() {
        return repliedAt;
    }

    public void setRepliedAt(Date repliedAt) {
        this.repliedAt = repliedAt;
    }

    public Integer getRepliedBy() {
        return repliedBy;
    }

    public void setRepliedBy(Integer repliedBy) {
        this.repliedBy = repliedBy;
    }
}
