package com.nutcracker.service.secret.impl;

import com.nutcracker.common.enums.SecretStrategyEnum;
import com.nutcracker.service.secret.SecretService;
import com.nutcracker.strategy.StrategyFactory;
import com.nutcracker.strategy.secret.BaseStrategy;
import com.nutcracker.util.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 加密解密操作
 *
 * @author 胡桃夹子
 * @date 2021-11-18 10:17
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SecretServiceImpl implements SecretService {

    private final StrategyFactory strategyFactory;

    @Override
    public String encrypt(SecretStrategyEnum secretStrategyEnum, String param) {
        BaseStrategy strategy = strategyFactory.getSecretStrategy(secretStrategyEnum);
        if (null != strategy) {
            return strategy.encrypt(param);
        }
        log.error("encrypt fail, {}, {}", secretStrategyEnum, param);
        return null;
    }

    @Override
    public String decrypt(SecretStrategyEnum secretStrategyEnum, String param) {
        BaseStrategy strategy = strategyFactory.getSecretStrategy(secretStrategyEnum);
        if (null != strategy) {
            return strategy.decrypt(param);
        }
        log.error("decrypt fail, {}, {}", secretStrategyEnum, param);
        return null;
    }

    @Override
    public String encrypt(SecretStrategyEnum secretStrategyEnum, List<String> list) {
        BaseStrategy strategy = strategyFactory.getSecretStrategy(secretStrategyEnum);
        if (null != strategy) {
            return strategy.execute(list, true);
        }
        log.error("encrypt list fail, {}, {}", secretStrategyEnum, JSON.toJSONString(list));
        return null;
    }

    @Override
    public String decrypt(SecretStrategyEnum secretStrategyEnum, List<String> list) {
        BaseStrategy strategy = strategyFactory.getSecretStrategy(secretStrategyEnum);
        if (null != strategy) {
            return strategy.execute(list, false);
        }
        log.error("decrypt list fail, {}, {}", secretStrategyEnum, JSON.toJSONString(list));
        return null;
    }
}
