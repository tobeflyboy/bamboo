package com.nutcracker.bamboo.infrastructure.mapper.auth;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.infrastructure.entity.auth.ConfigDo;

/**
 * 系统配置 访问层
 *
 * @author 胡桃夹子
 */
@Mapper
public interface ConfigMapper extends BaseMapper<ConfigDo> {

}
