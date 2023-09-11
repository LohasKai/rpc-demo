package com.mhz.rpc.consumer;

import com.mhz.rpc.api.IUserService;
import com.mhz.rpc.consumer.proxy.RPCClientProxy;
import com.mhz.rpc.pojo.User;

public class ClientBootstrap {
    public static void main(String[] args) {
        IUserService UserService = (IUserService)RPCClientProxy.createProxy(IUserService.class);
        User user = UserService.getUserById(2);
        System.out.println(user.toString());
    }
}
