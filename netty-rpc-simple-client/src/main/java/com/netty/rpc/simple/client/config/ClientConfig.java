package com.netty.rpc.simple.client.config;

import com.netty.rpc.client.ServiceBeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ClientConfig {

    @Bean
    public ServiceBeanDefinitionRegistry serviceBeanDefinitionRegistry(){
        return new ServiceBeanDefinitionRegistry();
    }
}
