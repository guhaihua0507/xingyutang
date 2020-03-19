package com.xingyutang.service;

import com.xingyutang.model.entity.User;

public interface UserService {
    public void createUser(User user);

    User getUserByOpenId(String openId);
}
