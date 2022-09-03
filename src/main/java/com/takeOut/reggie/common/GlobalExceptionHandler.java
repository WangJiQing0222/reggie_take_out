package com.takeOut.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author 小飞侠NO.1
 * @startTime 17:29:54
 */

@ControllerAdvice(annotations = {RestController.class, Controller.class})//只要类上面添加了这两个注解，就会调用此类
@ResponseBody   //方法将结果封装成json返回给
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        log.error("异常处理器方法调用...{}", ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return  R.error(msg);
        }

        return R.error("未知错误");
    }


    @ExceptionHandler(CustomeException.class)
    public R<String> exceptionHandler(CustomeException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
