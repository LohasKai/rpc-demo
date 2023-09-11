package com.mhz.rpc.provider.handler;

import com.alibaba.fastjson.JSON;
import com.mhz.rpc.common.RPCRequest;
import com.mhz.rpc.common.RPCResponse;
import com.mhz.rpc.provider.anno.RPCService;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务端业务处理类
 * 1.将标有@RPCService注解的bean缓存
 * 2.接受客户请求
 * 3.根据传过来的beanName从缓存中找到相应的bean
 * 4.解析请求中的方法名称、参数类型和参数信息
 * 5.反射调用bean的方法
 * 6.给客户端进行相应
 */
@Component
@ChannelHandler.Sharable
public class RPCServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {

    private static final Map<String, Object> SERVICE_INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 1.将标有@RPCService注解的bean缓存
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RPCService.class);
        if (serviceMap.size() > 0) {
            Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                Object serviceBean = entry.getValue();
                if (serviceBean.getClass().getInterfaces().length == 0){
                    throw new RuntimeException("服务必须实现接口");
                }
                //默认取第一个接口作为缓存bean的名称
                String name = serviceBean.getClass().getInterfaces()[0].getName();
                SERVICE_INSTANCE_MAP.put(name, serviceBean);
            }
        }

    }

    /**
     * 通道读取就绪事件
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        //1.接受客户请求   将msg转为RPCRequset对象
        RPCRequest rpcRequest = JSON.parseObject(msg, RPCRequest.class);
        RPCResponse rpcResponse = new RPCResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());
        try {
            rpcResponse.setResult(handler(rpcRequest));
        }catch (Exception e){
            e.printStackTrace();
            rpcResponse.setError(e.getMessage());
        }
        //6.给客户端进行相应
        ctx.writeAndFlush(JSON.toJSONString(rpcResponse));

    }

    /**
     * 业务处理逻辑
     * @param rpcRequest
     * @return
     */
    public Object handler(RPCRequest rpcRequest) throws InvocationTargetException {
        //3.根据传过来的beanName从缓存中找到相应的bean
        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());
        if (serviceBean == null){
            throw new RuntimeException("根据beanName找不到服务,beanName:"+rpcRequest.getClassName());
        }
        //4.解析请求中的方法名称、参数类型和参数信息
        Class<?> serviceBeanClass = serviceBean.getClass();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] parameters = rpcRequest.getParameters();
        //5.反射调用bean的方法 cglib反射调用
        FastClass fastClass = FastClass.create(serviceBeanClass);
        FastMethod method = fastClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceBean, parameters);
    }

}
