package com.ssu.ongi.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "mqtt")
public class MqttProperties {

    private String brokerUrl;
    private String username;
    private String password;
    private String clientId;
}
