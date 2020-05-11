package com.xingyutang.app.service;

import com.xingyutang.app.model.entity.User;

public interface UserService {
    public void createUser(User user);

    User getUserByOpenId(String openId);
}
