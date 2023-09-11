package com.mhz.rpc.consumer.proxy;


import com.alibaba.fastjson.JSON;
import com.mhz.rpc.common.RPCRequest;
import com.mhz.rpc.common.RPCResponse;
import com.mhz.rpc.consumer.client.RPCClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * 客户端代理类-创建代理对象
 * 1.封装Request请求对象
 * 2.创建RPCClient对象
 * 3.发送消息
 * 4.返回结果
 */
public class RPCClientProxy {

    public static Object createProxy(Class serviceClass){
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                //1.封装Request请求对象
                RPCRequest rpcRequest = new RPCRequest();
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setClassName(method.getDeclaringClass().getName());
                rpcRequest.setParameterTypes(method.getParameterTypes());
                rpcRequest.setParameters(args);
                rpcRequest.setMethodName(method.getName());
                //2.创建RPCClient对象
                RPCClient rpcClient = new RPCClient("127.0.0.1", 8899);
                try {
                //3.发送消息
                Object response = rpcClient.send(JSON.toJSONString(rpcRequest));
                //4.返回结果
                RPCResponse rpcResponse = JSON.parseObject(response.toString(), RPCResponse.class);
                if (rpcResponse.getError() != null){
                    throw new RuntimeException(rpcResponse.getError());
                }
                Object result = rpcResponse.getResult();
                return  JSON.parseObject(result.toString(), method.getReturnType());
                }catch (Exception e){
                    throw e;
                }finally {
                    rpcClient.close();
                }
            }
        });
    }

}
