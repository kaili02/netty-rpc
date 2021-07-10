package com.netty.rpc.server;

import com.netty.rpc.common.RpcRequest;
import com.netty.rpc.common.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Map;

@Slf4j
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> beanMap;

    private ApplicationContext applicationContext;

    public RpcHandler(Map<String, Object> beanMap, ApplicationContext applicationContext){
        this.beanMap = beanMap;
        this.applicationContext = applicationContext;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        log.info("server req:{}",request);
        // do work
        try {
            Object result = handle(request);
            response.setResult(result);
        } catch (Throwable throwable) {
            response.setError(throwable);
            log.error("channelRead0 error:", throwable);
        }
        ctx.writeAndFlush(response);
    }

    public Object handle(RpcRequest request) throws Throwable {
        String className = request.getClassName();

        Object serviceBean = beanMap.get(className);

        Method method = Class.forName(className).getMethod(request.getMethodName(), request.getParameterTypes());

        return  method.invoke(serviceBean, request.getParameters());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}

