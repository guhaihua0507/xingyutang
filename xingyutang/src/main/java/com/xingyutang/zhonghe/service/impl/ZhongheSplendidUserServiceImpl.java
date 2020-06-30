package com.xingyutang.zhonghe.service.impl;

import com.xingyutang.exception.RequestException;
import com.xingyutang.zhonghe.entity.ZhongheSplendidUser;
import com.xingyutang.zhonghe.mapper.ZhongheSplendidUserMapper;
import com.xingyutang.zhonghe.service.ZhongheSplendidUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;

@Service
public class ZhongheSplendidUserServiceImpl implements ZhongheSplendidUserService {
    @Autowired
    private ZhongheSplendidUserMapper zhongheSplendidUserMapper;

    @Override
    public ZhongheSplendidUser getUserByWxOpenId(String wxOpenId) {
        Condition condition = new Condition(ZhongheSplendidUser.class);
        condition.createCriteria().andEqualTo("wxOpenId", wxOpenId);
        return zhongheSplendidUserMapper.selectOneByExample(condition);
    }

    @Override
    public synchronized ZhongheSplendidUser makeAppointment(ZhongheSplendidUser user) throws RequestException {
        int count = getAppointmentCount(user.getVisitDate(), user.getVisitHour());
        if (count >= 50) {
            throw new RequestException("该时间段预约人数已满");
        }
        user.setCreateTime(new Date());
        zhongheSplendidUserMapper.insert(user);
        return user;
    }

    private int getAppointmentCount(Date date, int hour) {
        Condition condition = new Condition(ZhongheSplendidUser.class);
        condition.createCriteria().andEqualTo("visitDate", date).andEqualTo("visitHour", hour);
        return zhongheSplendidUserMapper.selectCountByExample(condition);
    }

}
