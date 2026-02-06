package com.nutcracker.bamboo.infrastructure.persistence.mapper.auth;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysConfigDo;

/**
 * 系统配置 访问层
 *
 * @author 胡桃夹子
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfigDo> {

}
