package com.example.springboot.entity;

import org.apache.poi.hpsf.Decimal;

import java.util.Date;

public class Teacher extends Account {
    private Integer id;                  // 主键ID
    private String username;             // 登录账号
    private String password;             // 登录密码
    private String name;                 // 老师姓名
    private String sex;                  // 性别
    private Date birthday;               // 出生日期
    private String phone;                // 手机号
    private String email;                // 邮箱地址
    private String avatar;               // 头像图片地址
    private String school;               // 所在学校
    private String subject;              // 教授科目
    private String descr;                // 个人简介
    private String qualificationPhotos;  // 教师资质证明图片地址
    private Date registrationTime;       // 注册时间
    private Integer totalClasses;        // 教学课程总数
    private Integer totalSubjects;       // 教授科目数
    private Double totalHours;           // 累计授课小时数
    private Double rating;

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    private String role;

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public String getQualificationPhotos() {
        return qualificationPhotos;
    }

    public void setQualificationPhotos(String qualificationPhotos) {
        this.qualificationPhotos = qualificationPhotos;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public Integer getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(Integer totalClasses) {
        this.totalClasses = totalClasses;
    }

    public Integer getTotalSubjects() {
        return totalSubjects;
    }

    public void setTotalSubjects(Integer totalSubjects) {
        this.totalSubjects = totalSubjects;
    }

    public Double getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Double totalHours) {
        this.totalHours = totalHours;
    }
} 