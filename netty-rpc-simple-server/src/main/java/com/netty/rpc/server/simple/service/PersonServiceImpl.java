package com.netty.rpc.server.simple.service;

import com.netty.rpc.annotation.RpcService;
import com.netty.rpc.common.Person;
import com.netty.rpc.interfaces.PersonService;

import java.util.ArrayList;
import java.util.List;

@RpcService(PersonService.class)
public class PersonServiceImpl implements PersonService {

    @Override
    public List<Person> callPerson(String name, Integer num) {
        List<Person> persons = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            persons.add(new Person(String.valueOf(i), name));
        }
        return persons;
    }
}

