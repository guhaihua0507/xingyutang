package com.xingyutang.zhonghe.service;

import com.xingyutang.exception.RequestException;
import com.xingyutang.ruihong.entity.RuihongAppointment;
import com.xingyutang.zhonghe.entity.ZhongheSplendidUser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ZhongheSplendidUserService {
    ZhongheSplendidUser getUserByWxOpenId(String wxOpenId);

    ZhongheSplendidUser makeAppointment(ZhongheSplendidUser user) throws RequestException;

}
