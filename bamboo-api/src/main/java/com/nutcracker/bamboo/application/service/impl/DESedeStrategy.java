package com.nutcracker.bamboo.application.service.impl;

import org.springframework.stereotype.Component;

import com.nutcracker.bamboo.application.service.BaseStrategy;
import com.nutcracker.shared.common.enums.SecretStrategyEnum;
import com.nutcracker.shared.util.secret.DESedeUtil;

/**
 * DESede加解密
 *
 * @author 胡桃夹子
 * @date 2021/11/17 18:04
 */
@Component
public class DESedeStrategy extends BaseStrategy {

    @Override
    public SecretStrategyEnum getSecretStrategyEnum() {
        return SecretStrategyEnum.DESede;
    }

    private String getKey() {
        return DESedeUtil.KEY;
    }

    @Override
    public String encrypt(String param) {
        return DESedeUtil.encrypt(getKey(), param);
    }

    @Override
    public String decrypt(String param) {
        return DESedeUtil.decrypt(getKey(), param);
    }
}
