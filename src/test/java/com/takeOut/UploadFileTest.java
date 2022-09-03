package com.takeOut;

import java.util.UUID;

/**
 * @author 小飞侠NO.1
 * @startTime 15:16:56
 */
public class UploadFileTest {
    public static void main(String[] args) {
        String s = UUID.randomUUID().toString();
        System.out.println(s);

        String str = "hello.jpg";
        String suffix = str.substring(str.lastIndexOf("."));
        System.out.println(s + suffix);
    }
}
