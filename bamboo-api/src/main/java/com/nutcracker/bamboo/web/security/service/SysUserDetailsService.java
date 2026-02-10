package com.nutcracker.bamboo.web.security.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.nutcracker.bamboo.application.service.auth.SysUserService;
import com.nutcracker.bamboo.common.util.JSON;
import com.nutcracker.bamboo.domain.model.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统用户认证 DetailsService
 *
 * @author 胡桃夹子
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SysUserDetailsService implements UserDetailsService {

    private final SysUserService userService;

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 用户名未找到异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            log.info("loadUserByUsername username={}", username);
            User sysUser = userService.findByUsername(username);
            if (sysUser == null) {
                throw new UsernameNotFoundException(username);
            }
            log.info("loadUserByUsername sysUser={}", JSON.toJSONString(sysUser));
            return new org.springframework.security.core.userdetails.User(sysUser.getUsername(), sysUser.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(sysUser.getRoleCode())));
        } catch (Exception e) {
            // 记录异常日志
            log.error("loadUserByUsername fail, username={}", username, e);
            // 抛出异常
            throw e;
        }
    }
}
