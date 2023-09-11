package com.mhz.rmi.client;

import com.mhz.rmi.pojo.User;
import com.mhz.rmi.service.IUserService;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RMIClient {
    public static void main(String[] args) throws RemoteException, NotBoundException {
        // 1.获取Registry实例
        Registry registry = LocateRegistry.getRegistry("localhost", 9998);
        // 2.通过Registry实例查找远程对象
        IUserService userService = (IUserService) registry.lookup("userService");
        User userById = userService.getUserById(2);
        System.out.println(userById.getId()+"---------------"+userById.getName());
    }
}
