package com.example.springboot.exception;

import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//告诉程序，这个异常处理器只负责处理 com.example.springboot.controller 这个 "文件夹" 下的 Controller 抛出的异常。
@ControllerAdvice("com.example.springboot.controller")
public class GlobalExceptionHandler {       //异常处理中心"，所有异常都送到这里来处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        e.printStackTrace();
        return Result.error();
    }


    //@ExceptionHandler(CustomerException.class) 注解，表示该方法专门处理 CustomerException 异常
    @ExceptionHandler(CustomerException.class)
    @ResponseBody
    public Result error(CustomerException e) {
        e.printStackTrace();
        return Result.error(e.getCode(),e.getMsg());
    }
}


//GlobalExceptionHandler 类是用于捕获和处理 CustomerException 异常的地方。
// 当在控制器中抛出 CustomerException 时，GlobalExceptionHandler 会接收到这个异常并进行处理。