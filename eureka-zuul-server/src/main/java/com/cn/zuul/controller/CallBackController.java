package com.cn.zuul.controller;

import com.alibaba.fastjson.JSONObject;
import com.cn.zuul.service.*;
import com.cn.zuul.common.CredentialType;
import com.cn.zuul.common.SystemClientInfo;
import com.cn.zuul.feign.OauthClient;
import com.cn.zuul.vo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/oauth")
@Slf4j
public class CallBackController {

    @Autowired
    private OauthClient client;

    @Autowired
    private QQAuthService qqAuthService;

    @Autowired
    private WeChatAuthService weChatAuthService;

    @Autowired
    private FaceBookAuthService faceBookAuthService;

    @Autowired
    private TwitterAuthService twitterAuthService;

    /**
     * qq第三方授权后会回调此方法，并将code传过来，然后通过code获取到qq第三方的token,通过token获取openID，
     * 中间可以先通过
     * 然后直接使用openID登录
     * @param code code
     * @return 包含accessToken,refreshToken等
     */
    @RequestMapping("/qq")
    public Map<String, Object> callbackQQ(@RequestParam(value = "code") String code) {
        if(code == null){
            throw new IllegalArgumentException("qq登录错误,回调code为空");
        }
        // 获取accessToken
        String token = qqAuthService.getAccessToken(code);
        if (token != null) {
            // 获取用户的openID
            String openid = qqAuthService.getOpenId(token);
            // 通过token和openID获取用户基本信息
            UserInfo usr = qqAuthService.getUserInfo(token,openid);
            if(usr == null){
                throw new IllegalArgumentException("qq登录错误，用户信息获取失败");
            }
            // 注册第三方用户，先通过openID判断是否存在该用户，若已存在则不重复注册
            client.ThirdRegister(usr);

            // 为了支持多类型登录，这里在username后拼装上登录类型，同时为了服务端校验，我们也拼上code
            String username = openid + "|" + CredentialType.QQ_OPENID.name() + "|" + code;
            // qq登录无需密码，但security底层有密码校验，我们这里将openid做用户名，code作为密码，认证中心采用同样规则即可
            String password = code;

            Map<String, Object> tokenInfo = this.login(username,password);
            log.debug("qq登录");

            return tokenInfo;
        }else {
            throw new IllegalArgumentException("qq登录错误，access_token获取失败");
        }
    }

    /**
     * 微信回调地址，也会返回个code
     * @param code
     */
    @RequestMapping("/wx")
    public Map<String, Object> callbackWx(String code) {
        if(code == null){
            throw new IllegalArgumentException("微信登录错误,回调code为空");
        }

        String result = weChatAuthService.getAccessToken(code);
        if(result == null){
            throw new IllegalArgumentException("微信登录错误,access_token获取失败");
        }
        JSONObject jsonObject = JSONObject.parseObject(result);

        String access_token = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openId");

        // 通过token和openID获取用户基本信息
        UserInfo usr = weChatAuthService.getUserInfo(access_token,openId);
        // 注册第三方用户，先通过openID判断是否存在该用户，若已存在则不重复注册
        if(usr == null){
            throw new IllegalArgumentException("微信登录错误，用户信息获取失败");
        }
        client.ThirdRegister(usr);

        // 为了支持多类型登录，这里在username后拼装上登录类型，同时为了服务端校验，我们也拼上code
        String username = openId + "|" + CredentialType.WX_OPENID.name() + "|" + code;
        // 微信登录无需密码，但security底层有密码校验，我们这里将openID做用户名，code作为密码，认证中心采用同样规则即可
        String password = code;


        Map<String, Object> tokenInfo = this.login(username,password);
        log.debug("qq登录");

        return tokenInfo;


    }


    /**
     * facebook回调地址，也会返回个code
     * @param code
     */
    @RequestMapping("/facebook")
    public Map<String, Object> callbackFacebook(String code) {
        if(code == null){
            throw new IllegalArgumentException("facebook登录错误,回调code为空");
        }
        String token = faceBookAuthService.getAccessToken(code);
        if(token == null){
            throw new IllegalArgumentException("facebook登录错误，access_token获取失败");
        }
        // facebook 没有openid，直接通过token去获取用户,用返回的用户id做唯一标示
        UserInfo usr = faceBookAuthService.getUserInfo(token,null);
        if(usr == null){
            throw new IllegalArgumentException("facebook登录错误，用户信息获取失败");
        }
        // 注册第三方用户，先通过openID判断是否存在该用户，若已存在则不重复注册
        client.ThirdRegister(usr);


        // 为了支持多类型登录，这里在username后拼装上登录类型，同时为了服务端校验，我们也拼上code
        String username =  usr.getOpenId() + "|" + CredentialType.FACEBOOK_ID.name() + "|" + code;
        // 微信登录无需密码，但security底层有密码校验，我们这里将openID做用户名，code作为密码，认证中心采用同样规则即可
        String password = code;

        Map<String, Object> tokenInfo = this.login(username,password);
        log.debug("qq登录");

        return tokenInfo;
    }

    /**
     * twitter 的回调接口，
     * @param verifier
     * @param token
     * @return
     */
    @RequestMapping("twitter")
    public Map<String ,Object> callbackTwitter(String verifier , String token){
        if(verifier == null|| token == null){
            throw new IllegalArgumentException("twitter登录错误,回调数据为空");
        }
        String code = verifier + "|" + token;
        // 获取twitter的access_token
        String result = twitterAuthService.getAccessToken(code);
        if(result == null){
            throw new IllegalArgumentException("facebook登录错误，access_token获取失败");
        }

        JSONObject jsonObject =  JSONObject.parseObject(result);

        // twitter 将access_token和user_id返回，因此user_id可做唯一标示去登录
        // accessToken 可缓存，然后调用twitter的其他官方接口
        String accessToken = jsonObject.getString("access_token");
        String userId = jsonObject.getString("user_id");

        // twitter 获取用户信息接口不明确 ，直接用userId注册，默认为男性
        UserInfo usr =  new UserInfo();
        usr.setOpenId(userId);
        usr.setGender(1);
        usr.setType(CredentialType.TWITTER_ID.name());
        // 注册第三方用户
        client.ThirdRegister(usr);

        // 为了支持多类型登录，这里在username后拼装上登录类型，同时为了服务端校验，我们也拼上code
        String username = userId + "|" + CredentialType.TWITTER_ID.name() + "|" + code;
        // twitter登录无需密码，但security底层有密码校验，我们这里将user_id做用户名，code作为密码，认证中心采用同样规则即可
        String password = code;
        Map<String, Object> tokenInfo = this.login(username,password);
        log.debug("twitter登录");

        return tokenInfo;
    }

    public Map<String,Object> login(String username, String password){
        Map<String, String> parameters = new HashMap<>();
        parameters.put(OAuth2Utils.GRANT_TYPE, "password");
        parameters.put(OAuth2Utils.CLIENT_ID, SystemClientInfo.CLIENT_ID);
        parameters.put("client_secret", SystemClientInfo.CLIENT_SECRET);
        parameters.put(OAuth2Utils.SCOPE, SystemClientInfo.CLIENT_SCOPE);
        // 为了支持多类型登录，这里在username后拼装上登录类型，同时为了服务端校验，我们也拼上code
        parameters.put("username", username);
        // twitter登录无需密码，但security底层有密码校验，我们这里将user_id做用户名，code作为密码，认证中心采用同样规则即可
        parameters.put("password", username);


        Map<String, Object> tokenInfo = client.postAccessToken(parameters);
        return tokenInfo;
    }

}
