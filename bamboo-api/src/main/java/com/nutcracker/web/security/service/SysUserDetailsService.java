package com.nutcracker.web.security.service;

import com.nutcracker.entity.domain.auth.SysUser;
import com.nutcracker.service.auth.SysUserService;
import com.nutcracker.util.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

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
            SysUser sysUser = userService.findByUsername(username);
            if (sysUser == null) {
                throw new UsernameNotFoundException(username);
            }
            log.info("loadUserByUsername sysUser={}", JSON.toJSONString(sysUser));
            return new User(sysUser.getUsername(), sysUser.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(sysUser.getRoleCode())));
        } catch (Exception e) {
            // 记录异常日志
            log.error("loadUserByUsername fail, username={}", username, e);
            // 抛出异常
            throw e;
        }
    }
}
