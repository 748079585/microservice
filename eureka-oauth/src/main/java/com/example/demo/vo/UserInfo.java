package com.example.demo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author luke
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {

    /**
     * 用户唯一标示 , twitter和facebook没有openid，用userId代替
     */
    private String openId;

    /**
     * 性别 1：男 2：女
     */
    private int gender;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 登录类型
     */
    private String type;
}
