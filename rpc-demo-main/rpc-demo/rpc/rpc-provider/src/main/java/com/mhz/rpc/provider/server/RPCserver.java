package com.mhz.rpc.provider.server;

import com.mhz.rpc.provider.handler.RPCServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RPCserver implements DisposableBean {

    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workGroup;

    @Autowired
    private RPCServerHandler rpcServerHandler;


    public void startServer(String id, int port){
        try {
             bossGroup = new NioEventLoopGroup(1);
             workGroup = new NioEventLoopGroup();
             ServerBootstrap serverBootstrap = new ServerBootstrap();
             serverBootstrap.group(bossGroup, workGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     ChannelPipeline pipeline = ch.pipeline();
                     pipeline.addLast(new StringDecoder());
                     pipeline.addLast(new StringEncoder());
                     pipeline.addLast(rpcServerHandler);
                 }
             });
             //绑定端口
            ChannelFuture sync = serverBootstrap.bind(id, port).sync();
            System.out.println("=========>服务端启动");
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            if (null!=bossGroup){
                bossGroup.shutdownGracefully();
            }
            if (workGroup != null) {
                workGroup.shutdownGracefully();
            }
        }
    }




    @Override
    public void destroy() {
        if (null!=bossGroup){
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
}
