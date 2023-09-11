package com.mhz.rpc.consumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.Callable;

public class RPCClientHandler extends SimpleChannelInboundHandler<String> implements Callable {

    ChannelHandlerContext channelHandlerContext;
    //发送的消息
    String RequestMsg;
    //服务端相应消息
    String ResponseMsg;



    public void setRequestMsg(String requestMsg) {
        RequestMsg = requestMsg;
    }

    /**
     * 通道连接就绪事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlerContext = ctx;
    }

    /**
     * 通道读取就绪事件
     * @param channelHandlerContext
     * @param msg
     * @throws Exception
     */
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        ResponseMsg = msg;
        //唤醒等待线程
        notify();
    }

    /**
     * 发送消息到服务端
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        channelHandlerContext.writeAndFlush(RequestMsg);
        wait();
        return ResponseMsg;
    }


}
