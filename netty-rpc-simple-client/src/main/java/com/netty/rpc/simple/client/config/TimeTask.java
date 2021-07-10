package com.netty.rpc.simple.client.config;

import com.netty.rpc.common.Person;
import com.netty.rpc.interfaces.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class TimeTask {

    @Autowired
    private PersonService personService;

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     *  定时调用
     */
    @Scheduled(fixedDelay = 5000)
    public void task(){

        log.info("call person api.");
        List<Person> list = personService.callPerson("kl", atomicInteger.getAndIncrement());
        log.info("result: {}", list);
    }
}
