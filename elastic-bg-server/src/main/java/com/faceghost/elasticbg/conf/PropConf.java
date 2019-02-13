package com.faceghost.elasticbg.conf;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@Configurable
@ConfigurationProperties(prefix = "elasticbg") //只注入以app开头的属性
public class PropConf {

    @Value("${elasticbg.email_host_name}")
    private String emailHostName;

    @Value("${elasticbg.email_use_ssl}")
    private String emailUseSsl;

    @Value("$elasticbg.email_ssl_port}")
    private String emailSslPort;

    @Value("${elasticbg.email_charset}")
    private String emailCharset;

    @Value("${elasticbg.email_user_name}")
    private String emailUserName;

    @Value("${elasticbg.email_user_pwd}")
    private String emailUserPwd;

    @Value("${elasticbg.machine}")
    private String machine;

    @Value("${elasticbg.env.readonly}")
    private Boolean envReadonly;
}
