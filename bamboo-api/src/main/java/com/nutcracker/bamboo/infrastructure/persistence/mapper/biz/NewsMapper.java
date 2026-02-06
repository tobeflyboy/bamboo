package com.nutcracker.bamboo.infrastructure.persistence.mapper.biz;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.infrastructure.persistence.entity.biz.NewsDo;


/**
 * 新闻mapper接口
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:33:12
 */
@Mapper
public interface NewsMapper extends BaseMapper<NewsDo> {

    List<NewsDo> findNewsByKeywords(@Param("keywords") String keywords);

    List<NewsDo> findNewsByPage(@Param("keywords") String keywords);

    List<NewsDo> findNewsByTitle(@Param("title") String title);
}
