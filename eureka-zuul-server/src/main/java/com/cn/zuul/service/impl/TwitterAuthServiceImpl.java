package com.cn.zuul.service.impl;

import com.cn.zuul.vo.UserInfo;
import com.cn.zuul.service.TwitterAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class TwitterAuthServiceImpl extends DefaultAuthServiceImpl implements TwitterAuthService {

    //要使用OAuth，首先要去Twitter中注册一个自己的Twitter应用，注册地址：http://dev.twitter.com/apps/new 。
    // 注册之后，会得到自己Twitter应用的Consumer key和Consumer secret，都是一个字符串。之后就可以进行OAuth的认证过程：

    private static final String CONSUMER_KEY = "xxx";
    private static final String CONSUMER_SECRET = "xxx";

    /**
     * Twitter应用把Consumer key和Consumer secret放入HTTP请求中发送到Twitter API： https://api.twitter.com/oauth/request_token ，得到一个Request Token
     */
    private static final String REQUEST_TOKEN_URL = "https://api.twitter.com/oauth/request_token";

    /**
     * Twitter应用重定向用户浏览器到 https://api.twitter.com/oauth/authorize?oauth_token=<Request Token> ，其中<Request Token>为在上面取得的Request Token
     */
    private static final String OAUTH_TOKEN_URL = "https://api.twitter.com/oauth/authorize?oauth_token=%s";

    /**
     * 获取access_token
     */
    private static final String ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";

    // Callback URL是注册Twitter应用需要填写的，也可以在2.1中在HTTP请求中和Consumer key和Consumer secret一起发送到Twitter API
    /**
     * 这个Callback URL是Twitter应用用于接收Verifier和新的Token的地址
     */
    private static final String CALLBACK_URL = "http://localhost:8000/oauth/twitter";

    //之后在使用每个Twitter API的时候，把Access Token附加于每一个Twitter API的HTTP请求中即可，Twitter API的使用，请见月光博客：https://www.williamlong.info/archives/2152.html


    @Override
    public String getAuthorizationUrl() {
        HashMap<String, String> params = new HashMap<>();
        //应用口令
        params.put("consumer_key", CONSUMER_KEY);
        params.put("consumer_secret", CONSUMER_SECRET);
        params.put("callback_url", CALLBACK_URL);
        String request_token = null;
        try {
            request_token = getRestTemplate().getForObject(REQUEST_TOKEN_URL, String.class ,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.format(OAUTH_TOKEN_URL, request_token);
    }

    @Override
    public String getAccessToken(String code) {
        String[] st = code.split("|");
        HashMap<String, String> params = new HashMap<>();
        //应用口令
        params.put("verifier", st[0]);
        params.put("token", st[1]);
        String request_token = null;
        try {
            request_token = getRestTemplate().getForObject(ACCESS_TOKEN_URL, String.class ,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request_token;
    }

    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }

    @Override
    public UserInfo getUserInfo(String accessToken, String openId) {

        return null;
    }
}
