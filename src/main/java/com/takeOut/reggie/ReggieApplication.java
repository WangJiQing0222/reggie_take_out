package com.takeOut.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author 小飞侠NO.1
 * @startTime 15:48:36
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan//扫描过滤器(servlet)，这样才能过滤器才能生效
@EnableTransactionManagement//使@Transactional生效
@EnableCaching//开启spring cache注解缓存功能
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("项目启动成功...");
    }
}
