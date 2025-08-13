package com.nutcracker.mapper;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.LocalDateTimeTypeHandler;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * 自定义日期类型处理程序
 *
 * @author 胡桃夹子
 * @date 2025/03/28 15:36:31
 */
public class CustomDateTypeHandler implements TypeHandler<LocalDateTime> {

    private static final LocalDateTimeTypeHandler DELEGATE = new LocalDateTimeTypeHandler();

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        DELEGATE.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        String str = rs.getString(columnName);
        return parseLocalDateTime(str);
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        String str = rs.getString(columnIndex);
        return parseLocalDateTime(str);
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String str = cs.getString(columnIndex);
        return parseLocalDateTime(str);
    }

    /**
     * 解析多种时间格式：支持 "yyyy-MM-dd HH:mm:ss.SSS" 和 "yyyy-MM-dd'T'HH:mm:ss.SSS"
     */
    private LocalDateTime parseLocalDateTime(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        // 把 ISO 格式转成标准格式
        str = str.trim().replace("T", " ");
        // 防止毫秒位数过多（如 .987000）
        str = str.replaceAll("\\.\\d{4,}", "");
        if (str.length() > 23) {
            // 截取到 .SSS
            str = str.substring(0, 23);
        }
        try {
            return LocalDateTime.parse(str, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(str, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e2) {
                throw new IllegalArgumentException("无法解析时间字符串: " + str, e2);
            }
        }
    }
}