package com.takeOut.reggie.config;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringToDateConvert implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(String s) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(s, formatter);
    }
}
