package com.netty.rpc.server.simple;

import com.netty.rpc.annotation.EnableRpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpcService
public class NettyRpcSimpleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyRpcSimpleServerApplication.class, args);
    }

}
