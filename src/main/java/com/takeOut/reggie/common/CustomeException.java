package com.takeOut.reggie.common;

/**
 * 自定义业务异常类
 * @author 小飞侠NO.1
 * @startTime 19:29:35
 */
public class CustomeException extends RuntimeException{
    public CustomeException(String message){
        super(message);
    }
}
