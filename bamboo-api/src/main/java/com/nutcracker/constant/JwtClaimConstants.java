package com.nutcracker.constant;

/**
 * JWT Claims声明常量
 * <p>
 * JWT Claims 属于 Payload 的一部分，包含了一些实体（通常指的用户）的状态和额外的元数据。
 *
 * @author 胡桃夹子
 */
public interface JwtClaimConstants {

    /**
     * 用户ID
     */
    String USER_ID = "userId";

    /**
     *账号
     */
    String USERNAME = "username";

    /**
     *姓名
     */
    String REAL_NAME = "realName";

    /**
     *状态
     */
    String STATUS = " status";

    /**
     * 角色id
     */
    String ROLE_ID = "roleId";

    /**
     *角色编码
     */
    String ROLE_CODE = "roleCode";

    /**
     *角色名称
     */
    String ROLE_NAME = "roleName";

}
