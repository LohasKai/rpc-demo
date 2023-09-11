package com.mhz.rpc.api;

import com.mhz.rpc.pojo.User;

public interface IUserService {
    /**
     * 根据用户id查找用户
     */
    User getUserById(int id);
}
