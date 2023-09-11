package com.mhz.rpc.provider.service;

import com.mhz.rpc.api.IUserService;
import com.mhz.rpc.pojo.User;
import com.mhz.rpc.provider.anno.RPCService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RPCService
@Service
public class UserServiceImpl implements IUserService {
    Map<Integer, User> userMap = new HashMap<>();

    @Override
    public User getUserById(int id) {
        if (userMap.isEmpty()){
            User zhangsan = new User(1, "张三");
            User lisi = new User(2, "lisi");
            userMap.put(zhangsan.getId(), zhangsan);
            userMap.put(lisi.getId(), lisi);
        }
        return userMap.get(id);
    }
}
