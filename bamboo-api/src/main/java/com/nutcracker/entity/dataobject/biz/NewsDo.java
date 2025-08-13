package com.nutcracker.entity.dataobject.biz;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nutcracker.mapper.CustomDateTypeHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * 新闻对象
 *
 * @author 胡桃夹子
 * @date 2022/3/15 14:10
 */
@Data
@TableName("t_news")
public class NewsDo implements Serializable {

    private static final long serialVersionUID = 3624947930970250778L;

    @TableId("id")
    private String id;

    /**
     * 新闻标题
     */
    @TableField("title")
    private String title;

    /**
     * 新闻内容
     */
    @TableField("description")
    private String description;

    /**
     * 新闻发生地址
     */
    @TableField("address")
    private String address;

    /**
     * 新闻发生时间
     */
    @TableField(value = "news_time", jdbcType = JdbcType.VARCHAR, typeHandler = CustomDateTypeHandler.class)
    private LocalDateTime newsTime;

    /**
     * 新闻发布时间
     */
    @TableField(value = "create_time", jdbcType = JdbcType.VARCHAR, typeHandler = CustomDateTypeHandler.class)
    private LocalDateTime createTime;

}
