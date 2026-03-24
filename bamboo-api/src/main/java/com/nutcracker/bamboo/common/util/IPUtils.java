package com.nutcracker.bamboo.common.util;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ip信息工具类
 *
 * @author 胡桃夹子
 * @date 2025/11/25
 */
@Slf4j
@Component
public class IPUtils {

    /**
     * ip2region v3.1.1 xdb 文件路径（放在 resources/db 目录下）
     */
    private static final String DB_PATH = "/db/ip2region_v4.xdb";

    /**
     * 全局共享的 Searcher。
     * ✔ newWithBuffer 是线程安全的
     * ✔ 可供多个线程并发查询
     * ✔ 性能最好（纯内存模式）
     */
    private static Searcher searcher;

    /**
     * Spring 容器加载完成后执行初始化逻辑
     * 使用：将 classpath 中的 ip2region.xdb 读取为 byte[]，然后使用 LongByteArray 包装，
     * 通过 newWithBuffer 创建纯内存查询器。
     */
    @PostConstruct
    public void init() {
        try {
            // 1. 从类路径读取 xdb 文件
            InputStream inputStream = getClass().getResourceAsStream(DB_PATH);
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + DB_PATH);
            }

            // 2. 读取为 byte[] 数据（一次性加载到内存）
            byte[] bytes = inputStream.readAllBytes();

            // 3. 转换为 LongByteArray：这是 3.1.1 要求的内存结构
            LongByteArray cBuff = new LongByteArray();
            cBuff.append(bytes);

            // 4. 使用纯内存方式创建 Searcher（性能最高、线程安全）
            searcher = Searcher.newWithBuffer(Version.IPv4, cBuff);

            log.info("ip2region 初始化完成（内存缓存模式）");

        } catch (Exception e) {
            log.error("IpRegionUtil initialization ERROR: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取客户端真实 IP（支持各种代理、Nginx、内网环境）
     */
    @SuppressWarnings("null")
    public static String getIpAddr(HttpServletRequest request) {
        String ip = null;
        try {
            if (request == null) {
                return "";
            }

            // 依次从常见 HTTP 头中获取
            ip = request.getHeader("x-forwarded-for");
            if (checkIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (checkIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }

            // 若都获取不到，则取远程地址
            if (checkIp(ip)) {
                ip = request.getRemoteAddr();

                // 本地访问情况，需还原本机真实 IP
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    ip = getLocalAddr();
                }
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR: {}", e.getMessage());
        }

        // 多代理情况 x-forwarded-for 可能会返回多个，用第一个即可
        if (StrUtil.isNotBlank(ip) && ip.contains(",")) {
            ip = ip.split(",")[0];
        }

        return ip;
    }

    /**
     * 判断 IP 是否为空或 unknown，方便统一判断
     */
    private static boolean checkIp(String ip) {
        return StrUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }

    /**
     * 获取本机 IP 地址（用于本地测试场景）
     */
    private static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost() ERROR: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 查询 IP 所在区域（国家|省份|城市|运营商）
     */
    public static String getRegion(String ip) {
        if (searcher == null) {
            log.error("Searcher is not initialized");
            return null;
        }

        try {
            return searcher.search(ip);
        } catch (Exception e) {
            log.error("IpRegionUtil search ERROR: {}", e.getMessage());
            return null;
        }
    }
}
