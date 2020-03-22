package com.xingyutang.app.service.impl;

import com.xingyutang.app.mapper.UserMapper;
import com.xingyutang.app.model.entity.User;
import com.xingyutang.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public void createUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public User getUserByOpenId(String openId) {
        Condition condition = new Condition(User.class);
        condition.createCriteria().andEqualTo("wxOpenId", openId);
        List<User> users = userMapper.selectByExample(condition);
        if (users != null && users.size() > 0) {
            return users.get(0);
        }
        return null;
    }
}
