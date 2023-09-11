package com.mhz.rmi.server;

import com.mhz.rmi.service.UserServiceImpl;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIServer {
    public static void main(String[] args) {
        // 1.注册Registry实例，绑定端口
        try {
            Registry registry = LocateRegistry.createRegistry(9998);
            // 2.创建远程对象
            UserServiceImpl userService = new UserServiceImpl();
            // 3.将远程对象注册到RMI服务器商
            registry.bind("userService", userService);
            System.out.println("=================》RMI服务端启动了");
        } catch (RemoteException | AlreadyBoundException e) {
            e.printStackTrace();
        }
    }
}
