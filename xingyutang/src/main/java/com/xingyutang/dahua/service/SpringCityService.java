package com.xingyutang.dahua.service;

import com.xingyutang.dahua.entity.SpringCityAward;
import com.xingyutang.dahua.entity.SpringCityUser;
import com.xingyutang.dahua.entity.SpringCityUserAward;
import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.vo.FolidayUserVo;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface SpringCityService {


    SpringCityUser signIn(SpringCityUser user);

    SpringCityUser getUserByWxOpenId(String wxOpenId);

    SpringCityUser getUserById(Long id);

    void addPowerByWxUser(Long userId, String wxOpenId);

    List<SpringCityAward> getAwards();

    @Transactional
    void claimAward(Long userId, Integer awardType);

    SpringCityUserAward getUserAward(Long userId);

    List<SpringCityUser> listAllUsers();

    InputStream exportAll() throws IOException;
}
