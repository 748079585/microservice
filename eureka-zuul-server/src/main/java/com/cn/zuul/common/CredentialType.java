package com.cn.zuul.common;

/**
 * 用户账号登录类型
 * @author luke
 */
public enum CredentialType {

    /**
     * 用户名密码
     */
    USERNAME,

    /**
     * 微信openid
     */
    WX_OPENID,

    /**
     * qq openid
     */
    QQ_OPENID,

    /**
     * facebook id
     */
    FACEBOOK_ID,

    /**
     * twitter token
     */
    TWITTER_ID,

}
