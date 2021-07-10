package com.netty.rpc.Serializer;

public abstract class Serializer {
    public abstract <T> byte[] serialize(T obj);
    public abstract <T> Object deserialize(byte[] bytes, Class<T> clazz);
}
