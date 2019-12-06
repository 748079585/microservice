package com.cn.zuul.controller;

import com.cn.zuul.service.*;
import com.cn.zuul.feign.OauthClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author luke
 */
@Controller
@Slf4j
@RequestMapping("/sys")
public class LoginController {

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
     * 用户密码登录接口
     * @param clientId  客户端ID
     * @param secret    客户端密码
     * @param scope     客户端范围
     * @param username  用户名s
     * @param password  密码
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> passwordLogin(String clientId, String secret, String scope , String username , String password){
        Map<String, String> parameters = new HashMap<>();
        parameters.put(OAuth2Utils.GRANT_TYPE, "password");
        parameters.put(OAuth2Utils.CLIENT_ID, clientId);
        parameters.put("client_secret", secret);
        parameters.put(OAuth2Utils.SCOPE, scope);
        parameters.put("username", username);
        parameters.put("password", password);

        Map<String, Object> tokenInfo = client.postAccessToken(parameters);
        log.debug("用户名密码登录");

        return tokenInfo;

    }

    /**
     * 退出
     *
     * @param access_token
     */
    @DeleteMapping("/logout")
    public ResponseEntity logout(String access_token, @RequestHeader(required = false, value = "Authorization") String token) {
        if (StringUtils.isBlank(access_token)) {
            if (StringUtils.isNoneBlank(token)) {
                access_token = token.substring(OAuth2AccessToken.BEARER_TYPE.length() + 1);
            }else{
                throw new IllegalArgumentException("无token");
            }
        }

        System.out.println(access_token);
        client.removeToken(access_token);
        return new ResponseEntity("退出成功", HttpStatus.OK);
    }

    /**   * qq第三方登录
     * @return 重定向到qq第三方登录的页面
     */
    @GetMapping("/login-qq")
    public RedirectView oauthByQQ(){
        String url = qqAuthService.getAuthorizationUrl();
        return new RedirectView(url);
    }

    /**
     * 微信第三方登录
     * @return 重定向到微信第三方登录的页面
     */
    @GetMapping("/login-wx")
    public String oauthByWx() {
        String url = weChatAuthService.getAuthorizationUrl();
        // 重定向到微信的登录界面
        return "redirect:" + url;
    }

    @GetMapping("/login-fb")
    public String oauthByFb(){
        String url = faceBookAuthService.getAuthorizationUrl();
        return "redirect:" + url ;
    }

    @GetMapping("/login-twitter")
    public RedirectView oauthByTwitter(){
        String url = twitterAuthService.getAuthorizationUrl();
        return new RedirectView(url);
    }
}