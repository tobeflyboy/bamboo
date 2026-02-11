package com.nutcracker.bamboo.application.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 微信小程序手机号登录请求参数
 *
 * @author 有来技术团队
 * @since 2.0.0
 */
@Schema(description = "微信小程序手机号登录请求参数")
@Data
public class WxMiniAppPhoneLoginDTO {

    @Schema(description = "微信小程序登录时获取的code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "包括敏感数据在内的完整用户信息的加密数据")
    private String encryptedData;

    @Schema(description = "加密算法的初始向量")
    private String iv;
    
    // 手动添加getter方法以解决编译问题
    public String getCode() {
        return this.code;
    }
    
    public String getEncryptedData() {
        return this.encryptedData;
    }
    
    public String getIv() {
        return this.iv;
    }

} 