package com.netty.rpc.server;

import com.netty.rpc.Serializer.Serializer;
import com.netty.rpc.annotation.RpcService;
import com.netty.rpc.codec.RpcDecoder;
import com.netty.rpc.codec.RpcEncoder;
import com.netty.rpc.common.RpcRequest;
import com.netty.rpc.common.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class NettyServer extends Server implements ApplicationContextAware, InitializingBean, DisposableBean {

    public NettyServer(){
        log.info("init NettyServer");
    }
    static ApplicationContext applicationContext;

    private Thread thread;

    private Serializer serializer;

    private static Map<String, Object> beanMap = new ConcurrentHashMap();

    /**
     *  这里注册bean信息到容器
     *
     * @param applicationContext applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        NettyServer.applicationContext = applicationContext;
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RpcService.class);

        if (MapUtils.isNotEmpty(beansWithAnnotation)) {
            for (Object serviceBean : beansWithAnnotation.values()) {
                String serviceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                beanMap.put(serviceName, serviceBean);
            }
        }
        serializer = applicationContext.getBean(Serializer.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }

    @Override
    public void destroy() throws Exception {
        log.info("netty server stop.");
        stop();
    }

    @Override
    public void start() {
        log.info("netty server start.");
        startServer();
    }

    private void startServer(){
        thread = new Thread(() -> {
            NioEventLoopGroup bossGroup = new NioEventLoopGroup();
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();

            ServerBootstrap bootstrap = new ServerBootstrap();
            try {
                bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline()
                                        .addLast(new RpcDecoder(RpcRequest.class, serializer))   // 注册解码
                                        .addLast(new RpcEncoder(RpcResponse.class, serializer))   // 注册编码
                                        .addLast(new RpcHandler(beanMap, applicationContext));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                String serverAddress = applicationContext.getEnvironment().getProperty("rpc.server.address");
                String[] addressSplit = serverAddress.split(":");
                String host = addressSplit[0];
                String port = addressSplit[1];

                ServiceRegister serviceRegister = applicationContext.getBean(ServiceRegister.class);
                if (serviceRegister != null) {
                    serviceRegister.register(serverAddress);
                }
                ChannelFuture channelFuture = bootstrap.bind(host, Integer.valueOf(port)).sync();

                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                workerGroup.shutdownGracefully();
                bossGroup.shutdownGracefully();
            }
        });

        thread.start();
    }

    @Override
    public void stop() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }
}

