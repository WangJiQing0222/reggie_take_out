package com.takeOut.reggie.common;

/**
 * @author 小飞侠NO.1
 * @startTime 21:38:21
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
