package com.example.demo.service.impl;

import com.example.demo.constans.CredentialType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.entity.LoginAppUser;
import com.example.demo.service.AppUserService;

/**
 *
 */
/**
 *  根据用户名获取用户<br>
 * 密码校验请看下面两个类
 *
 * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 * @author lei
 * @date 2019/08/16
 */
@Slf4j
@Service("userDetailsService")
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private AppUserService userClient;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 为了支持多类型登录，这里username后面拼装上登录类型,如username|type
        String[] params = username.split("\\|");
        // 真正的用户名
        username = params[0];

        LoginAppUser loginAppUser = userClient.findByUsername(username);
        if (loginAppUser == null) {
            throw new AuthenticationCredentialsNotFoundException("用户不存在");
        } else if (!loginAppUser.isEnabled()) {
            throw new DisabledException("用户已作废");
        }

        if (params.length > 1) {
            // 登录类型
            CredentialType credentialType = CredentialType.valueOf(params[1]);
            if (params.length < 3) {
                throw new IllegalArgumentException("非法请求");
            }
            if (CredentialType.QQ_OPENID == credentialType) {
                // qq登录
                handlerQQLogin(loginAppUser, params);
            } else if (CredentialType.WX_OPENID == credentialType) {
                // 微信登陆
                handlerWXLogin(loginAppUser, params);
            } else if (CredentialType.FACEBOOK_ID == credentialType) {
                // facebook登录
                handlerFacebookLogin(loginAppUser, params);
            } else if (CredentialType.TWITTER_TOKEN == credentialType) {
                // twitter登录
                handlerTwitterLogin(loginAppUser, params);
            }
        }

        return loginAppUser;
    }

    private void handlerQQLogin(LoginAppUser loginAppUser, String[] params) {

        String openid = params[0];
        String tempCode = params[2];

        // 这里做个数据库验证，通过openid 和 tempCode 验证下

        // 其实这里是将密码重置，网关层的微信登录接口，密码也用同样规则即可
        loginAppUser.setPassword(passwordEncoder.encode(tempCode));
        log.info("QQ登陆，{},{}", loginAppUser, openid);
    }

    private void handlerWXLogin(LoginAppUser loginAppUser, String[] params) {

        String openid = params[0];
        String tempCode = params[2];

        // 这里做个数据库验证，通过openid 和 tempCode 验证下

        // 其实这里是将密码重置，网关层的微信登录接口，密码也用同样规则即可
        loginAppUser.setPassword(passwordEncoder.encode(tempCode));
        log.info("微信登陆，{},{}", loginAppUser, openid);
    }
    private void handlerFacebookLogin(LoginAppUser loginAppUser, String[] params) {

        String openid = params[0];
        String tempCode = params[2];

        // 这里做个数据库验证，通过openid 和 tempCode 验证下

        // 其实这里是将密码重置，网关层的微信登录接口，密码也用同样规则即可
        loginAppUser.setPassword(passwordEncoder.encode(tempCode));
        log.info("facebook登陆，{},{}", loginAppUser, openid);
    }
    private void handlerTwitterLogin(LoginAppUser loginAppUser, String[] params) {

        String openid = params[0];
        String tempCode = params[2];

        // 这里做个数据库验证，通过openid 和 tempCode 验证下

        // 其实这里是将密码重置，网关层的微信登录接口，密码也用同样规则即可
        loginAppUser.setPassword(passwordEncoder.encode(tempCode));
        log.info("twitter登陆，{},{}", loginAppUser, openid);
    }
}
