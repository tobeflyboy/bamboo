package com.nutcracker.bamboo.application.service.impl;

import org.springframework.stereotype.Component;

import com.nutcracker.bamboo.application.service.BaseStrategy;
import com.nutcracker.shared.common.enums.SecretStrategyEnum;
import com.nutcracker.shared.util.secret.DesUtil;

/**
 * DES加解密接口
 *
 * @author 胡桃夹子
 * @date 2021/11/17 18:04
 */
@Component
public class DESStrategy extends BaseStrategy {

    @Override
    public SecretStrategyEnum getSecretStrategyEnum() {
        return SecretStrategyEnum.DES;
    }

    @Override
    public String encrypt(String param) {
        return DesUtil.encrypt(param);
    }

    @Override
    public String decrypt(String param) {
        return DesUtil.decrypt(param);
    }
}
