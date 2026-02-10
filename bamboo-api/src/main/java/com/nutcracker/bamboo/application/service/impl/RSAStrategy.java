package com.nutcracker.bamboo.application.service.impl;

import org.springframework.stereotype.Component;

import com.nutcracker.bamboo.application.service.BaseStrategy;
import com.nutcracker.bamboo.common.enums.SecretStrategyEnum;
import com.nutcracker.bamboo.common.util.secret.RsaUtil;

/**
 * RSA加解密
 *
 * @author 胡桃夹子
 * @date 2021/11/17 18:04
 */
@Component
public class RSAStrategy extends BaseStrategy {

    @Override
    public SecretStrategyEnum getSecretStrategyEnum() {
        return SecretStrategyEnum.RSA;
    }

    @Override
    public String encrypt(String param) {
        return RsaUtil.encrypt(param);
    }

    @Override
    public String decrypt(String param) {
        return RsaUtil.decrypt(param);
    }
}
