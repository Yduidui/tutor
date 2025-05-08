package com.example.springboot.common;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@Component
//把这个类交给 Spring 容器管理，相当于自动注册成一个 Bean。
//之后就可以通过 @Autowired 或者 @Resource 自动注入这个配置类使用了。
//
//@ConfigurationProperties(prefix = "alipay")
//告诉 Spring Boot：我要从 application.yml 或 application.properties 中读取以 alipay 开头的配置项，然后把它们注入到这个类的属性里。

@Component
@ConfigurationProperties(prefix = "alipay")
public class AlipayConfig {

    private String appId;
    private String appPrivateKey;
    private String alipayPublicKey;
    private String notifyUrl;       //支付宝通知本地的接口完整地址

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppPrivateKey() {
        return appPrivateKey;
    }

    public void setAppPrivateKey(String appPrivateKey) {
        this.appPrivateKey = appPrivateKey;
    }

    public String getAlipayPublicKey() {
        return alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}



