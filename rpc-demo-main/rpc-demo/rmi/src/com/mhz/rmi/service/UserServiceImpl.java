package com.mhz.rmi.service;

import com.mhz.rmi.pojo.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class UserServiceImpl extends UnicastRemoteObject implements IUserService {

    Map<Integer, User> userMap = new HashMap<>();

    public UserServiceImpl() throws RemoteException {
        super();
        User zhangsan = new User(1, "张三");
        User lisi = new User(2, "李四");
        userMap.put(zhangsan.getId(), zhangsan);
        userMap.put(lisi.getId(), lisi);
    }

    @Override
    public User getUserById(int id) throws RemoteException {
        return userMap.get(id);
    }
}
