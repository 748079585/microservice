package com.cn.zuul.feign;

import java.util.Map;

import com.cn.zuul.vo.AppUser;
import com.cn.zuul.vo.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用feign远程调用oauth-server的接口
 * @author luke
 */
@FeignClient("oauth-server")
public interface OauthClient {

    /**
     * 获取access_token
     * 这是spring-security-oauth2底层的接口，类TokenEndpoint
     * @param parameters
     * @return
     * @see org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
     */
    @PostMapping(path = "/oauth/token")
    Map<String, Object> postAccessToken(@RequestParam Map<String, String> parameters);

    /**
     * 删除access_token和refresh_token<br>
     * 认证中心的OAuth2Controller方法removeToken
     *
     * @param accessToken
     */
    @DeleteMapping(path = "/remove_token")
    void removeToken(@RequestParam("access_token") String accessToken);

    /**
     * 使用第三方用户注册
     * @param userInfo
     * @return
     */
    @PostMapping("/users-anon/third/register")
    AppUser ThirdRegister(@RequestBody UserInfo userInfo);

}
