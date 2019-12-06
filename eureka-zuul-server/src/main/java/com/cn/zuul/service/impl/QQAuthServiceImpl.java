package com.cn.zuul.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.zuul.common.CredentialType;
import com.cn.zuul.vo.UserInfo;
import com.cn.zuul.service.QQAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QQAuthServiceImpl extends DefaultAuthServiceImpl implements QQAuthService {
    /**
     * QQ 登陆页面的URL
     */
    private final static String AUTHORIZATION_URL =
            "https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=%s";
    /**
     * 获取token的URL
     */
    private final static String ACCESS_TOKEN_URL = "https://graph.qq.com/oauth2.0/token?grant_type=authorization_code&client_id=%s&client_secret=%s&code=%s&redirect_uri=%s";
    /**
     * 获取用户 openid 的 URL
     */
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me?access_token=%s";
    /**
     * 获取用户信息的 URL，oauth_consumer_key 为 apiKey
     */
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info?access_token=%s&oauth_consumer_key=%s&openid=%s";
    // 下面的属性可以通过配置读取
    /**
     * QQ 在登陆成功后回调的 URL，这个 URL 必须在 QQ 互联里填写过
     */
    private static final String CALLBACK_URL = "http://localhost:8000/oauth/qq";
    /**
     *  QQ 互联应用管理中心的 APP ID
     */
    private static final String APP_ID = "111111111";
    /**
     * QQ 互联应用管理中心的 APP Key
     */
    private static final String APP_SECRET = "111111111111111111111111111111111";
    /**
     * QQ 互联的 API 接口，访问用户资料
     */
    private static final String SCOPE = "get_user_info";

    @Override
    public String getAuthorizationUrl() {
        return String.format(AUTHORIZATION_URL, APP_ID, CALLBACK_URL, SCOPE);
    }

    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKEN_URL, APP_ID, APP_SECRET, code, CALLBACK_URL);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp != null && resp.contains("access_token")) {
            Map<String, String> map = getParam(resp);
            return map.get("access_token");
        }
        log.error("QQ获得access_token失败，code无效，resp:{}", resp);
        return null;
    }

    /**
     * 由于QQ的几个接口返回类型不一样，此处是获取key-value类型的参数
     * @param resp
     * @return map
     */
    private Map<String, String> getParam(String resp) {
        HashMap<String, String> map = new HashMap<>();
        String[] kvArray = resp.split("&");
        for (String s : kvArray) {
            String[] kv = s.split("=");
            map.put(kv[0], kv[1]);
        }
        return map;
    }

    /**
     * QQ接口返回类型是text/plain，此处将其转为json
     * @param string
     * @return JSONObject
     */
    private JSONObject ConvertToJson(String string) {
        string = string.substring(string.indexOf("(") + 1);
        string = string.substring(0, string.indexOf(")"));
        return JSONObject.parseObject(string);
    }

    @Override
    public String getOpenId(String accessToken) {
        String url = String.format(OPEN_ID_URL, accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().getForObject(uri, String.class);
        if (resp != null && resp.contains("openid")) {
            JSONObject jsonObject = ConvertToJson(resp);
            String openid = jsonObject.getString("openid");
            return openid;
        }
        log.error("QQ获得openid失败，accessToken无效，resp:{}", resp);
        return null;
    }

    @Override
    public UserInfo getUserInfo(String accessToken, String openId) {
        String url = String.format(USER_INFO_URL, accessToken, APP_ID, openId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String resp = getRestTemplate().getForObject(uri, String.class);
        JSONObject data = JSONObject.parseObject(resp);
        UserInfo result = new UserInfo();
        result.setOpenId(openId);
        // qq返回gender默认为男
        int gender = 1;
        if (data.getString("gender").equals("女")){
            gender = 2;
        }
        result.setGender(gender);
        // figureurl_qq_1 大小为40×40像素的QQ头像URL。
        result.setAvatar(data.getString("figureurl_qq_1"));
        result.setNickname(data.getString("nickname"));
        result.setType(CredentialType.QQ_OPENID.name());

        // 这里调用用户服务，先通过openid判断是否存在该用户，不存在就保存这个基本用户

        return result;
    }

    @Override
    public String refreshToken(String code) {
        return null;
    }
}
