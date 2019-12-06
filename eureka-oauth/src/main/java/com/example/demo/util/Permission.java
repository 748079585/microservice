package com.example.demo.util;

/**
 * @author luke
 */
public enum Permission {

    // 权限管理权限列表
    /**
     * 保存权限
     */
    PERMISSION_SAVE("back:permission:save" , "保存权限标识"),
    /**
     * 更新权限
     */
    PERMISSION_UPDATE("back:permission:update" , "更新权限标识"),
    /**
     * 删除权限
     */
    PERMISSION_DELETE("back:permission:delete" , "删除权限标识"),
    /**
     * 查询权限
     */
    PERMISSION_QUERY("back:permission:query" , "查询权限标识"),

    // 角色管理权限列表
    /**
     * 保存权限
     */
    ROLE_SAVE("back:role:save" , "保存角色标识"),
    /**
     * 更新权限
     */
    ROLE_UPDATE("back:role:update" , "更新角色标识"),
    /**
     * 删除权限
     */
    ROLE_DELETE("back:role:delete" , "删除角色标识"),
    /**
     * 查询权限
     */
    ROLE_QUERY("back:role:query" , "查询角色标识"),
    /**
     * 设置角色权限
     */
    ROLE_PERMISSION_SET("back:role:permission:set" , "给角色分配权限"),
    /**
     *  获取角色权限
     */
    ROLE_PERMISSION_FIND_BY_ID("role:permission:byRoleId" , "获取用户权限"),

    // 用户管理权限列表
    /**
     * 查询用户
     */
    USER_QUERY("back:user:query" , "查询用户标识"),
    /**
     * 设置用户密码
     */
    USER_PASSWORD("back:user:password" , "用户重置密码"),
    /**
     * 更新用户
     */
    USER_UPDATE("back:user:update" , "更新用户标识"),
    /**
     * 给用户分配角色
     */
    USER_role_set("back:user:role:set" , "给用户分配角色"),

    /**
     * 获取用户角色
     */
    USER_ROLE_ID("user:role:id" , "获取用户角色"),

    // 投诉管理权限列表

    COMPLAINTS_COMPLAINT_SAVE("complaints:complaint:save","保存投诉信息"),

    COMPLAINTS_COMPLAINT_QUERY("complaints:complaint:query","查询投诉信息"),

    COMPLAINTS_COMPLAINT_FIND_BY_ID("complaints:complaint:findById","通过ID查询投诉信息"),

    COMPLAINTS_COMPLAINT_DELETE("complaints:complaint:delete","删除投诉信息"),

    COMPLAINTS_REASON_SAVE("complaints:reason:save","保存投诉原因"),

    COMPLAINTS_REASON_DELETE("complaints:reason:delete","删除投诉原因"),

    COMPLAINTS_REASON_UPDATE("complaints:reason:update","更新投诉原因"),

    COMPLAINTS_REASON_QUERY("complaints:reason:query","查询投诉原因");

    /**
     * 权限标识
     */
    private String code;

    /**
     * 标识信息
     */
    private String message;

    Permission(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode(){
        return this.code;
    }

    public String getMessage(){
        return this.message;
    }

}
