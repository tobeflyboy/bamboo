package com.nutcracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;

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
        Environment env = ctx.getEnvironment();

        String appName = ctx.getId();
        String profile = String.join(", ", env.getActiveProfiles());
        String contextPath = env.getProperty("server.servlet.context-path", "");
        int port = env.getProperty("server.port", Integer.class, 8080);

        String host = "localhost";
        try {
            host = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception ignored) {
        }

        // 获取 BuildProperties（来自 spring-boot-maven-plugin build-info）
        String version = "UNKNOWN";
        try {
            BuildProperties bp = ctx.getBean(BuildProperties.class);
            version = bp.getVersion();
        } catch (Exception ignored) {
        }

        String baseUrl = "http://" + host + ":" + port + contextPath;

        log.info("""
                        
                        ------------------------------------------------------------
                         Application      : {}
                         Version          : {}
                         Active Profile   : {}
                         Context Path     : {}
                         Access URL       : {}
                         API Doc          : {}/doc.html
                        ------------------------------------------------------------
                        """,
                appName,
                version,
                profile.isBlank() ? "default" : profile,
                contextPath,
                baseUrl,
                baseUrl
        );
    }


}