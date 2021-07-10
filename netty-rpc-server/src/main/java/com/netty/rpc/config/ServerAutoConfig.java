package com.netty.rpc.config;

import com.netty.rpc.Serializer.ProtostuffSerializer;
import com.netty.rpc.Serializer.Serializer;
import com.netty.rpc.server.NettyServer;
import com.netty.rpc.server.ServiceRegister;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;


public class ServerAutoConfig {

    @Value("${zookeeper.url:127.0.0.1:2181}")
    String registerAddress;
    @Value("${zookeeper.register.path.prefix:/data}")
    String dataPath;

    @Bean
    @DependsOn({"serializer","serviceRegister"})
    public NettyServer nettyServer(){
        return new NettyServer();
    }

    @Bean
    @ConditionalOnMissingBean(Serializer.class)
    public Serializer serializer(){
        return new ProtostuffSerializer();
    }

    @Bean
    public ServiceRegister serviceRegister(){
        return new ServiceRegister(registerAddress, dataPath);
    }
}
