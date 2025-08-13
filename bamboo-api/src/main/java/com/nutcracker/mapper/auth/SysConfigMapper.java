package com.nutcracker.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.entity.dataobject.auth.SysConfigDo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统配置 访问层
 *
 * @author 胡桃夹子
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfigDo> {

}
