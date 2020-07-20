package com.xingyutang.dahua.service.impl;

import com.xingyutang.dahua.entity.AnniversaryPrizePool;
import com.xingyutang.dahua.entity.AnniversaryUser;
import com.xingyutang.dahua.entity.AnniversaryUserPrize;
import com.xingyutang.dahua.mapper.AnniversaryPrizePoolMapper;
import com.xingyutang.dahua.mapper.AnniversaryUserMapper;
import com.xingyutang.dahua.mapper.AnniversaryUserPrizeMapper;
import com.xingyutang.dahua.service.AnniversaryUserService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AnniversaryUserServiceImpl implements AnniversaryUserService {
    private final static int[] PRIZE_WEIGHT = {8, 16, 83, 83, 833, 8977};

    @Autowired
    private AnniversaryUserMapper userMapper;
    @Autowired
    private AnniversaryPrizePoolMapper prizePoolMapper;
    @Autowired
    private AnniversaryUserPrizeMapper userPrizeMapper;

    private Map<Long, AnniversaryPrizePool> prizePoolMap = null;

    @Override
    public AnniversaryUser signIn(AnniversaryUser entity) {
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userMapper.insert(entity);
        return entity;
    }

    @Override
    public AnniversaryUser getUserByOpenId(String wxOpenId) {
        Condition condition = new Condition(AnniversaryUser.class);
        condition.and().andEqualTo("wxOpenId", wxOpenId);
        return userMapper.selectOneByExample(condition);
    }

    @Override
    public AnniversaryUserPrize getUserPrize(Long userId, Date date) {
        Date startTime = DateUtils.truncate(date, Calendar.DATE);
        Date endTime = DateUtils.addSeconds(DateUtils.addDays(startTime, 1), -1);

        Condition condition = new Condition(AnniversaryUserPrize.class);
        condition.createCriteria().andEqualTo("userId", userId)
                .andBetween("createTime", startTime, endTime);

        return userPrizeMapper.selectOneByExample(condition);
    }

    @Override
    public List<AnniversaryUserPrize> getUserPrizeList(Long userId) {
        Condition condition = new Condition(AnniversaryUserPrize.class);
        condition.createCriteria().andEqualTo("userId", userId);
        condition.setOrderByClause("create_time desc");
        return userPrizeMapper.selectByExample(condition);
    }

    @Override
    public synchronized Integer drawLottery(Long userId) {
        int prizeIndex = getRandomPrize();
        int prize = prizeIndex + 1;
        List<AnniversaryPrizePool> prizePools = getAllPrizePool();
        for (int i = 0; i < prizePools.size(); i++) {
            AnniversaryPrizePool prizePool = prizePools.get(i);
            if (prizePool.getId().equals(prize)) {
                if (prizePool.getAmount() > 0) {
                    prizePool.setAmount(prizePool.getAmount() - 1);
                    prizePoolMapper.updateByPrimaryKey(prizePool);
                    insertUserPrize(userId, prize);
                    return prize;
                }
            } else {
                break;
            }
        }

        for (int i = prizePools.size() - 1; i >= 0; i--) {
            AnniversaryPrizePool prizePool = prizePools.get(i);
            if (prizePool.getAmount() > 0) {
                prizePool.setAmount(prizePool.getAmount() - 1);
                prizePoolMapper.updateByPrimaryKey(prizePool);
                insertUserPrize(userId, prizePool.getId().intValue());
                return prizePool.getId().intValue();
            }
        }

        return null;
    }

    private void insertUserPrize(Long userId, Integer prize) {
        AnniversaryUserPrize userPrize = new AnniversaryUserPrize();
        userPrize.setUserId(userId);
        userPrize.setPrizeId(Long.valueOf(prize));
        userPrize.setCreateTime(new Date());
        userPrizeMapper.insert(userPrize);
    }

    private int getRandomPrize() {
        int total = 0;
        for (int w : PRIZE_WEIGHT) {
            total += w;
        }
        int randomValue = RandomUtils.nextInt(0, total);
        int start = 0;
        int prize = -1;
        for (int i = 0; i < PRIZE_WEIGHT.length; i++) {
            int end = start + PRIZE_WEIGHT[i];
            if (randomValue >= start && randomValue < end) {
                prize = i;
                break;
            }
            start = end;
        }
        return prize;
    }

    @Override
    public List<AnniversaryPrizePool> getAllPrizePool() {
        Condition condition = new Condition(AnniversaryPrizePool.class);
        condition.setOrderByClause("id asc");
        return prizePoolMapper.selectByExample(condition);
    }
}
