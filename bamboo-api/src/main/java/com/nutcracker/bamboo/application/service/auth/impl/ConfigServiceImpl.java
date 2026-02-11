package com.nutcracker.bamboo.application.service.auth.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nutcracker.bamboo.application.model.query.ConfigQuery;
import com.nutcracker.bamboo.application.service.auth.ConfigService;
import com.nutcracker.bamboo.common.constant.RedisConstants;
import com.nutcracker.bamboo.domain.model.entity.Config;
import com.nutcracker.bamboo.infrastructure.converter.auth.ConfigConvert;
import com.nutcracker.bamboo.infrastructure.entity.auth.ConfigDo;
import com.nutcracker.bamboo.infrastructure.mapper.auth.ConfigMapper;
import com.nutcracker.bamboo.web.security.util.SecurityUtils;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

/**
 * 系统配置Service接口实现
 *
 * @author 胡桃夹子
 * @since 2024-07-29 11:17:26
 */
@Service
@RequiredArgsConstructor
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, ConfigDo> implements ConfigService {

    private final ConfigConvert sysConfigConvert;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 系统启动完成后，加载系统配置到缓存
     */
    @PostConstruct
    public void init() {
        refreshCache();
    }

    /**
     * 分页查询系统配置
     *
     * @param configPageQuery 查询参数
     * @return 系统配置分页列表
     */
    @Override
    public IPage<Config> page(ConfigQuery configPageQuery) {
        Page<ConfigDo> page = new Page<>(configPageQuery.getPageNum(), configPageQuery.getPageSize());
        String keywords = configPageQuery.getKeywords();
        LambdaQueryWrapper<ConfigDo> query = new LambdaQueryWrapper<ConfigDo>()
                .and(StringUtils.isNotBlank(keywords),
                        q -> q.like(ConfigDo::getConfigKey, keywords)
                                .or()
                                .like(ConfigDo::getConfigName, keywords)
                );
        Page<ConfigDo> pageList = this.page(page, query);
        return sysConfigConvert.toPageVo(pageList);
    }

    /**
     * 保存系统配置
     *
     * @param configForm 系统配置表单
     * @return 是否保存成功
     */
    @Override
    public boolean save(Config configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<ConfigDo>().eq(ConfigDo::getConfigKey, configForm.getConfigKey())) == 0,
                "配置键已存在");
        ConfigDo config = sysConfigConvert.toEntity(configForm);
        config.setCreateBy(SecurityUtils.getUserId());
        config.setIsDeleted(0);
        return this.save(config);
    }

    /**
     * 获取系统配置表单数据
     *
     * @param id 系统配置ID
     * @return 系统配置表单数据
     */
    @Override
    public Config getConfigFormData(Long id) {
        ConfigDo entity = this.getById(id);
        return sysConfigConvert.toForm(entity);
    }

    /**
     * 编辑系统配置
     *
     * @param id         系统配置ID
     * @param configForm 系统配置表单
     * @return 是否编辑成功
     */
    @Override
    public boolean edit(Long id, Config configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<ConfigDo>().eq(ConfigDo::getConfigKey, configForm.getConfigKey()).ne(ConfigDo::getId, id)) == 0,
                "配置键已存在");
        ConfigDo config = sysConfigConvert.toEntity(configForm);
        config.setUpdateBy(SecurityUtils.getUserId());
        return this.updateById(config);
    }

    /**
     * 删除系统配置
     *
     * @param id 系统配置ID
     * @return 是否删除成功
     */
    @Override
    public boolean delete(Long id) {
        if (id != null) {
            return super.update(new LambdaUpdateWrapper<ConfigDo>()
                    .eq(ConfigDo::getId, id)
                    .set(ConfigDo::getIsDeleted, 1)
                    .set(ConfigDo::getUpdateBy, SecurityUtils.getUserId())
            );
        }
        return false;
    }

    /**
     * 刷新系统配置缓存
     *
     * @return 是否刷新成功
     */
    @Override
    public boolean refreshCache() {
        redisTemplate.delete(RedisConstants.System.CONFIG);
        List<ConfigDo> list = this.list();
        if (list != null) {
            Map<String, String> map = list.stream().collect(Collectors.toMap(ConfigDo::getConfigKey, ConfigDo::getConfigValue));
            redisTemplate.opsForHash().putAll(RedisConstants.System.CONFIG, map);
            return true;
        }
        return false;
    }

    /**
     * 获取系统配置
     *
     * @param key 配置键
     * @return 配置值
     */
    @Override
    public Object getSystemConfig(String key) {
        if (StringUtils.isNotBlank(key)) {
            return redisTemplate.opsForHash().get(RedisConstants.System.CONFIG, key);
        }
        return null;
    }

}
