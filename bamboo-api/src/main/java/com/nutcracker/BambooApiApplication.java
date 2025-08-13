package com.nutcracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.context.WebApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.StringJoiner;

/**
 * Start Application
 *
 * @author 胡桃夹子
 * @date 2022/1/29 11:01
 */
@Slf4j
@EnableAsync
@EnableCaching
@SpringBootApplication(scanBasePackages = "com.nutcracker")
@ServletComponentScan(basePackages = "com.nutcracker")
public class BambooApiApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(BambooApiApplication.class, args);
        printApplicationInfo(context);
    }

    private static void printApplicationInfo(ApplicationContext ctx) {
        StringJoiner joiner = new StringJoiner("\n\t", "\n\n\t", "\n\t");
        joiner.add("SpringBoot Version: " + Optional.ofNullable(SpringApplication.class.getPackage()).map(Package::getImplementationVersion).orElse("UNKNOWN"));
        joiner.add("Application Version: " + Optional.ofNullable(BambooApiApplication.class.getPackage()).map(Package::getImplementationVersion).orElse("UNKNOWN"));
        // 文档URL处理
        String contextPath = Optional.ofNullable(ctx.getEnvironment().getProperty("server.servlet.context-path")).orElse("");
        if (ctx instanceof WebApplicationContext) {
            Environment env = ctx.getEnvironment();
            try {
                String host = InetAddress.getLocalHost().getHostAddress();
                int port = env.getProperty("server.port", Integer.class, 8080);
                joiner.add("Doc URL: http://" + host + ":" + port + contextPath + "/doc.html");
            } catch (UnknownHostException e) {
                log.warn("Host address resolution failed, skipping doc URL", e);
            }
        }
        // 启动成功消息
        String appName = contextPath.isBlank() ? "" : contextPath.replace("/", "") + " ";
        joiner.add(appName + "Start Successful");
        log.info(joiner.toString());
    }
}