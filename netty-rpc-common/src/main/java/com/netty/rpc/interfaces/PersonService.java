package com.netty.rpc.interfaces;

import com.netty.rpc.annotation.RpcClient;
import com.netty.rpc.common.Person;

import java.util.List;

@RpcClient
public interface PersonService {
    List<Person> callPerson(String name, Integer num);
}

