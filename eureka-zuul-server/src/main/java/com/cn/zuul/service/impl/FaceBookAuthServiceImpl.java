package com.cn.zuul.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.zuul.common.CredentialType;
import com.cn.zuul.vo.UserInfo;
import com.cn.zuul.service.FaceBookAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;

@Service
@Slf4j
public class FaceBookAuthServiceImpl extends DefaultAuthServiceImpl implements FaceBookAuthService {

    /**
     * 应用编号
     */
    private static final String CLIENT_ID ="应用编号";
    /**
     * 应用秘钥
     */
    private static final String CLIENT_SECRET ="应用秘钥";
    /**
     * 回调地址，登录成功回掉到自己服务器的地址
     */
    private static final String REDIRECT_URL ="http://localhost:8000/oauth/facebook";
    /**
     * 获取临时口令
     */
    private static final String CODE_URL ="https://www.facebook.com/v2.8/dialog/oauth?client_id=%s&redirect_uri=%s";
    /**
     * 获取访问口令
     */
    private static final String TOKEN_URL ="https://graph.facebook.com/v2.8/oauth/access_token?client_id=%s&redirect_uri=%s&client_secret=%s&code=%s";
    /**
     * 获取用户信息
     */
    private static final String USER_URL ="https://graph.facebook.com/me?access_token=%s&fields=%s";
    /**
     * 验证口令
     */
    private static final String VERIFY_URL ="https://graph.facebook.com/debug_token";
    /**
     * 获取应用口令
     */
    private static final String APP_URL ="https://graph.facebook.com/v2.8/oauth/access_token?client_id=%s&client_secret=%s&grant_type=%s";

    // 重定向此地址
    @Override
    public String getAuthorizationUrl(){
        String url = String.format(CODE_URL, CLIENT_ID, REDIRECT_URL);
        return url;
    }

    /**
     * 用code换取accessToken
     * @param code code
     * @return
     */
    @Override
    public String getAccessToken(String code) {
        String url = String.format(TOKEN_URL, CLIENT_ID, CLIENT_SECRET, CLIENT_SECRET,code);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String[] responseResult = getRestTemplate().getForObject(uri, String[].class);
        String accessToken = null;
        if (null != responseResult && responseResult[0].equals("200")) {
            String result = responseResult[1];
            JSONObject jsonObject =  JSONObject.parseObject(result);
            accessToken = jsonObject.getString("access_token");
        }
        return accessToken;
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
        String fields="id,name,gender";
        String url = String.format(USER_URL, accessToken, fields);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();
        String[] responseResult =null;
        JSONObject userInfo=null;
        UserInfo result = new UserInfo();
        try {
            responseResult = getRestTemplate().getForObject(uri, String[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != responseResult && "200".equals(responseResult[0])) {
            String data = responseResult[1];
            userInfo =  JSONObject.parseObject(data);
            // facebook使用userId当作openid
            result.setOpenId(userInfo.getString("id"));
            // 性别默认为男性
            int gender = 1;
            if(userInfo.getString("gender").equals("female")){
                gender = 2;
            }
            result.setGender(gender);
            result.setNickname(userInfo.getString("name"));
            result.setType(CredentialType.FACEBOOK_ID.name());

        }
        return result;
    }

    /**
     * accessToken 检验口令
     * 调用图谱API，验证口令  app_id 和 user_id 字段将帮助您的应用确认访问口令对用户和您的应用有效。
     * 验证访问的用户是否来自你的应用，防刷功能，防止恶意注册
     * return data;
     */
    public String verifyToken(String accessToken) {
        HashMap<String, String> params = new HashMap<>();
        //应用口令
        String access_token = getAppToken();
        params.put("input_token", accessToken);
        params.put("access_token", access_token);
        String[] responseResult = null;
        String data = null;
        try {
            responseResult = getRestTemplate().getForObject(VERIFY_URL, String[].class ,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != responseResult && responseResult[0].equals("200")) {
            String result = responseResult[1];
            JSONObject jsonObject = JSONObject.parseObject(result);
            data = jsonObject.getString("data");
            System.out.println(data);
        }
//		{
//		    "data": {
//		        "app_id": 138483919580948,
//		        "application": "Social Cafe",
//		        "expires_at": 1352419328,
//		        "is_valid": true,
//		        "issued_at": 1347235328,
//		        "metadata": {
//		            "sso": "iphone-safari"
//		        },
//		        "scopes": [
//		            "email",
//		            "publish_actions"
//        ],
//		        "user_id": 1207059
//		    }
//		}
        return data;
    }

    /**
     * 获取应用口令（用来验证口令是否来自我的应用）
     * @return String
     */
    public String getAppToken(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("client_id", CLIENT_ID);
        params.put("client_secret", CLIENT_SECRET);
        params.put("grant_type", "client_credentials");
        String[] responseResult =null;
        String appToken=null;
        try {
            responseResult = getRestTemplate().getForObject(APP_URL, String[].class ,params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != responseResult && responseResult[0].equals("200")) {
            String result = responseResult[1];
            JSONObject jsonObject =  JSONObject.parseObject(result);
            appToken = jsonObject.getString("access_token");
            System.out.println(appToken);
        }
        return appToken;
    }

}

