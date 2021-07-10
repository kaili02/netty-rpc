package com.netty.rpc.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Setter
@Getter
//@ConfigurationProperties(prefix="zookeeper.register")
@EnableConfigurationProperties
public class ServerProperies {

    private String address = "127.0.0.1";

    private int port = 12181;

    private String dataPath = "/data";
}

