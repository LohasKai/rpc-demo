package com.mhz.rpc.consumer.client;


import com.mhz.rpc.consumer.handler.RPCClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.concurrent.*;

/**
 * 客户端
 * 1.连接netty服务端
 * 2.提供给调用者主动关闭资源的方法
 * 3.提供消息发送的方法
 */
public class RPCClient {

    private NioEventLoopGroup bossGroup;

    private Channel channel;

    private String ip;

    private int port;

    private  RPCClientHandler rpcClientHandler = new RPCClientHandler();

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public RPCClient(String ip, int port) throws InterruptedException {
        this.ip = ip;
        this.port = port;
        initClient();
    }



    /**
     * 初始化客户端
     */
    public void initClient() throws InterruptedException {
        bossGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        try {
            //设置参数
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, Boolean.TRUE)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            pipeline.addLast(rpcClientHandler);
                        }
                    });
            //连接netty服务端
            channel = bootstrap.connect(ip, port).sync().channel();
        }catch (Exception e){
            e.printStackTrace();
            if (null != bossGroup){
                channel.close();
            }
            if (null != bossGroup){
                bossGroup.shutdownGracefully();
            }
        }
    }
    /**
     * 关闭
     */
    public void close(){
        if (null != bossGroup){
            channel.close();
        }
        if (null != bossGroup){
            bossGroup.shutdownGracefully();
        }
    }
    /**
     * 发送
     */
    public Object send(String msg) throws ExecutionException, InterruptedException {
        rpcClientHandler.setRequestMsg(msg);
        Future submit = executorService.submit(rpcClientHandler);
        return submit.get();
    }

}
