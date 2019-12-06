# config-center
微服务
config 配置中心：修改完git上配置文件后，使用post方法向配置中心“/actuator/bus-refresh"发送请求，微服务中通过@RefreshScope注解,可对该类下所有配置属性自动更新

自动刷新配置：Github  Settings中WebHooks ，Add webHook，配置"http://configIp:port/actuator/bus-refresh"路径，勾选push.
配置成功后，会在每次 push 代码后，都会给远程 HTTP URL 发送一个 POST 请求。
上面的URL需要使用外网可以访问到的地址
  
在git 上修改配置，修改提交后， Webhooks 就会自动发送一个POST请求到你配置的URL，也可以看到日志在跑了，说明调用成功，然后再访问之前测试配置文件更新的地址，可以拿到修改后的配置.
