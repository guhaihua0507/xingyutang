package com.xingyutang.dahua.service;

import com.xingyutang.dahua.entity.AnniversaryPrizePool;
import com.xingyutang.dahua.entity.AnniversaryUser;
import com.xingyutang.dahua.entity.AnniversaryUserPrize;

import java.util.Date;
import java.util.List;

public interface AnniversaryUserService {
    void resetPrizePool();

    AnniversaryUser signIn(AnniversaryUser entity);

    AnniversaryUser getUserByOpenId(String wxOpenId);

    AnniversaryUserPrize getUserPrize(Long userId, Date date);

    Integer drawLottery(Long userId);

    List<AnniversaryPrizePool> getAllPrizePool();

    List<AnniversaryUserPrize> getUserPrizeList(Long userId);
}
