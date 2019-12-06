package com.gt.complaints.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  测试自动刷新配置功能
 *  使用@RefreshScope注解
 * @author luke
 */
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Value("${complaint.test}")
    private String abc;

    @GetMapping("/hi")
    public String test(){
        return abc;
    }
}
