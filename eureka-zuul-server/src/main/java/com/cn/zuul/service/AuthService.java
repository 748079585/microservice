package com.cn.zuul.service;

import com.cn.zuul.vo.UserInfo;

/**
 * 第三方登录
 */
public interface AuthService {
    /**
     * 根据code获得Token
     *
     * @param code code
     * @return token
     */
    String getAccessToken(String code);
    /**
     * 根据Token获得OpenId
     *
     * @param accessToken Token
     * @return openId
     */
    String getOpenId(String accessToken);
    /**
     * 刷新Token
     *
     * @param code code
     * @return 新的token
     */
    String refreshToken(String code);
    /**
     * 拼接授权URL
     *
     * @return URL
     */
    String getAuthorizationUrl();
    /**
     * 根据Token和OpenId获得用户信息
     *
     * @param accessToken Token
     * @param openId openId
     * @return 第三方应用给的用户信息
     */
    UserInfo getUserInfo(String accessToken, String openId);
}
