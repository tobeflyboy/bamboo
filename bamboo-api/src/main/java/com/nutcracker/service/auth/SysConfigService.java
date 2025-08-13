package com.nutcracker.service.auth;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nutcracker.entity.dataobject.auth.SysConfigDo;
import com.nutcracker.entity.domain.auth.SysConfig;
import com.nutcracker.entity.query.auth.ConfigPageQuery;

/**
 * 系统配置Service接口
 *
 * @author 胡桃夹子
 * @since 2024-07-29 11:17:26
 */
public interface SysConfigService extends IService<SysConfigDo> {

    /**
     * 分页查询系统配置
     * @param sysConfigPageQuery 查询参数
     * @return 系统配置分页列表
     */
    IPage<SysConfig> page(ConfigPageQuery sysConfigPageQuery);

    /**
     * 保存系统配置
     * @param sysConfigForm 系统配置表单
     * @return 是否保存成功
     */
    boolean save(SysConfig sysConfigForm);

    /**
     * 获取系统配置表单数据
     *
     * @param id 系统配置ID
     * @return 系统配置表单数据
     */
    SysConfig getConfigFormData(Long id);

    /**
     * 编辑系统配置
     * @param id  系统配置ID
     * @param sysConfigForm 系统配置表单
     * @return 是否编辑成功
     */
    boolean edit(Long id, SysConfig sysConfigForm);

    /**
     * 删除系统配置
     * @param ids 系统配置ID
     * @return 是否删除成功
     */
    boolean delete(Long ids);

    /**
     * 刷新系统配置缓存
     * @return 是否刷新成功
     */
    boolean refreshCache();

    /**
     * 获取系统配置
     * @param key 配置键
     * @return 配置值
     */
    Object getSystemConfig(String key);

}
