package com.netty.rpc.client;

import com.netty.rpc.common.RpcRequest;
import com.netty.rpc.common.RpcResponse;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

public class RpcProxy<T> implements FactoryBean<T> {

    private String serverAddress;

    private Class<T> interfaceType;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    private RpcClient rpcClient;

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class<?>[] { interfaceType }, (proxy, method, args) -> {

                    if (Object.class == method.getDeclaringClass()) {
                        String name = method.getName();
                        if ("equals".equals(name)) {
                            return proxy == args[0];
                        } else if ("hashCode".equals(name)) {
                            return System.identityHashCode(proxy);
                        } else if ("toString".equals(name)) {
                            return proxy.getClass().getName() + "@" +
                                    Integer.toHexString(System.identityHashCode(proxy)) +
                                    ", with InvocationHandler " + this;
                        } else {
                            throw new IllegalStateException(String.valueOf(method));
                        }
                    }

                    //创建RpcRequest，封装被代理类的属性
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    //拿到声明这个方法的业务接口名称
                    request.setClassName(method.getDeclaringClass()
                            .getName());
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    synchronized (this) {
                        if (rpcClient == null) {
                            //查找服务
                            if (serviceDiscovery != null) {
                                serverAddress = serviceDiscovery.discover();
                            }
                            //随机获取服务的地址
                            String[] array = serverAddress.split(":");
                            String host = array[0];
                            int port = Integer.parseInt(array[1]);
                            //创建Netty实现的RpcClient，链接服务端
                            rpcClient = new RpcClient(host, port);
                        }
                    }
                    //通过netty向服务端发送请求
                    RpcResponse response = rpcClient.send(request);
                    //返回信息
                    if (Objects.nonNull(response.getError())) {
                        throw response.getError();
                    } else {
                        return response.getResult();
                    }
                });
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

}


