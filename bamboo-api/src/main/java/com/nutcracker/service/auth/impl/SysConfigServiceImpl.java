package com.nutcracker.service.auth.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nutcracker.constant.RedisConstants;
import com.nutcracker.web.security.util.SecurityUtils;
import com.nutcracker.entity.convert.auth.SysConfigConvert;
import com.nutcracker.entity.dataobject.auth.SysConfigDo;
import com.nutcracker.entity.domain.auth.SysConfig;
import com.nutcracker.entity.query.auth.ConfigPageQuery;
import com.nutcracker.mapper.auth.SysConfigMapper;
import com.nutcracker.service.auth.SysConfigService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统配置Service接口实现
 *
 * @author 胡桃夹子
 * @since 2024-07-29 11:17:26
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfigDo> implements SysConfigService {

    private final SysConfigConvert sysConfigConvert;
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
    public IPage<SysConfig> page(ConfigPageQuery configPageQuery) {
        Page<SysConfigDo> page = new Page<>(configPageQuery.getPageNum(), configPageQuery.getPageSize());
        String keywords = configPageQuery.getKeywords();
        LambdaQueryWrapper<SysConfigDo> query = new LambdaQueryWrapper<SysConfigDo>()
                .and(StringUtils.isNotBlank(keywords),
                        q -> q.like(SysConfigDo::getConfigKey, keywords)
                                .or()
                                .like(SysConfigDo::getConfigName, keywords)
                );
        Page<SysConfigDo> pageList = this.page(page, query);
        return sysConfigConvert.toPageVo(pageList);
    }

    /**
     * 保存系统配置
     *
     * @param configForm 系统配置表单
     * @return 是否保存成功
     */
    @Override
    public boolean save(SysConfig configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<SysConfigDo>().eq(SysConfigDo::getConfigKey, configForm.getConfigKey())) == 0,
                "配置键已存在");
        SysConfigDo config = sysConfigConvert.toEntity(configForm);
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
    public SysConfig getConfigFormData(Long id) {
        SysConfigDo entity = this.getById(id);
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
    public boolean edit(Long id, SysConfig configForm) {
        Assert.isTrue(
                super.count(new LambdaQueryWrapper<SysConfigDo>().eq(SysConfigDo::getConfigKey, configForm.getConfigKey()).ne(SysConfigDo::getId, id)) == 0,
                "配置键已存在");
        SysConfigDo config = sysConfigConvert.toEntity(configForm);
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
            return super.update(new LambdaUpdateWrapper<SysConfigDo>()
                    .eq(SysConfigDo::getId, id)
                    .set(SysConfigDo::getIsDeleted, 1)
                    .set(SysConfigDo::getUpdateBy, SecurityUtils.getUserId())
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
        List<SysConfigDo> list = this.list();
        if (list != null) {
            Map<String, String> map = list.stream().collect(Collectors.toMap(SysConfigDo::getConfigKey, SysConfigDo::getConfigValue));
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
