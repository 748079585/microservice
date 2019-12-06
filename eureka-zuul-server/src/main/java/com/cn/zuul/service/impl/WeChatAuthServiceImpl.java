package com.cn.zuul.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cn.zuul.common.CredentialType;
import com.cn.zuul.vo.UserInfo;
import com.cn.zuul.service.WeChatAuthService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Slf4j
public class WeChatAuthServiceImpl extends DefaultAuthServiceImpl implements WeChatAuthService {

    private Logger logger = LoggerFactory.getLogger(WeChatAuthServiceImpl.class);

    /**
     * 请求此地址即跳转到二维码登录界面
     */
    private static final String AUTHORIZATION_URL =
            "https://open.weixin.qq.com/connect/qrconnect?appid=%s&redirect_uri=%s&response_type=code&scope=%s&state=%s#wechat_redirect";

    /**
     * 获取用户 openid 和access——toke 的 URL
     */
    private static final String ACCESS_TOKE_OPENID_URL =
            "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";

    /**
     * 刷新token
     */
    private static final String REFRESH_TOKEN_URL =
            "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=%s&grant_type=refresh_token&refresh_token=%s";

    /**
     * 获取用户信息
     */
    private static final String USER_INFO_URL =
            "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s&lang=zh_CN";

    private static final String APP_ID="xxxxxx";
    private static final String APP_SECRET="xxxxxx";
    private static final String SCOPE = "snsapi_login";

    /**
     * 回调域名
     */
    private String callbackUrl = "https://localhost:8000/oauth/wx";

    @Override
    public String getAuthorizationUrl(){
        String url = String.format(AUTHORIZATION_URL,APP_ID,callbackUrl,SCOPE, System.currentTimeMillis());
        return url;
    }


    @Override
    public String getAccessToken(String code) {
        String url = String.format(ACCESS_TOKE_OPENID_URL,APP_ID,APP_SECRET,code);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        logger.error("getAccessToken resp = "+resp);
        JSONObject res = new JSONObject();
        if(resp.contains("openid")){
            JSONObject jsonObject = JSONObject.parseObject(resp);
            String access_token = jsonObject.getString("access_token");
            String openId = jsonObject.getString("openid");;

            res.put("access_token",access_token);
            res.put("openId",openId);
            res.put("refresh_token",jsonObject.getString("refresh_token"));
        }else{
            log.error("获取token失败，msg = "+resp);
        }
        return res.toJSONString();
    }

    //微信接口中，token和openId是一起返回，故此方法不需实现
    @Override
    public String getOpenId(String accessToken) {
        return null;
    }

    @Override
    public UserInfo getUserInfo(String accessToken, String openId){
        String url = String.format(USER_INFO_URL, accessToken, openId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        String resp = getRestTemplate().getForObject(uri, String.class);
        UserInfo result = new UserInfo();
        if(resp.contains("errcode")){
            log.error("获取用户信息错误，msg = "+resp);
        }else{
            JSONObject data =JSONObject.parseObject(resp);

            result.setOpenId(openId);
            // 微信返回的性别，1为男性，2为女性
            result.setGender(Integer.parseInt(data.getString("gender")));
            result.setAvatar(data.getString("headimgurl"));
            result.setNickname(data.getString("nickname"));
            result.setType(CredentialType.WX_OPENID.name());
        }
        return result;
    }

    //微信的token只有2小时的有效期，过时需要重新获取，所以官方提供了
    //根据refresh_token 刷新获取token的方法，本项目仅仅是获取用户
    //信息，并将信息存入库，所以两个小时也已经足够了
    @Override
    public String refreshToken(String refresh_token) {

        String url = String.format(REFRESH_TOKEN_URL,APP_ID,refresh_token);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.build().encode().toUri();

        ResponseEntity<JSONObject> resp = getRestTemplate().getForEntity(uri,JSONObject.class);
        JSONObject jsonObject = resp.getBody();

        String access_token = jsonObject.getString("access_token");
        return access_token;
    }
}