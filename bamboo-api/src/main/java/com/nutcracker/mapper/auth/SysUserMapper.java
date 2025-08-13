package com.nutcracker.mapper.auth;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.entity.dataobject.auth.SysUserDo;
import com.nutcracker.entity.domain.auth.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:39:40
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDo> {

    /**
     * 根据用户名查询用户
     *
     * @param user {@link SysUser }
     * @return {@link SysUser }
     */
    List<SysUser> findLoginUser(SysUser user);

    /**
     * 根据用户名查询用户
     *
     * @param roleCode 用户名
     * @return {@link List }<{@link SysUserDo }>
     */
    List<SysUserDo> findUserByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据条件（店铺、名称）查询客服人员
     *
     * @param user {@link SysUser }
     * @return {@link List }<{@link SysUserDo }>
     */
    List<SysUser> findUser(SysUser user);
}
