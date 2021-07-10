package com.netty.rpc.simple.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class NettyRpcSimpleClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyRpcSimpleClientApplication.class, args);
    }

}
