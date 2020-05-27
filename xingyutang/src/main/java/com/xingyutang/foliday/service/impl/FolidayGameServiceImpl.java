package com.xingyutang.foliday.service.impl;

import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameCoin;
import com.xingyutang.foliday.mapper.FolidayGameCoinMapper;
import com.xingyutang.foliday.mapper.FolidayGameMapper;
import com.xingyutang.foliday.service.FolidayGameService;
import com.xingyutang.foliday.vo.FolidayUserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.util.Date;
import java.util.List;

@Service
public class FolidayGameServiceImpl implements FolidayGameService {

    @Autowired
    private FolidayGameMapper folidayGameMapper;
    @Autowired
    private FolidayGameCoinMapper folidayGameCoinMapper;

    @Override
    public FolidayGame signIn(FolidayUserVo userVo) {
        FolidayGame entity = new FolidayGame();
        entity.setUserId(userVo.getUserId());
        entity.setName(userVo.getName());
        entity.setPhoneNumber(userVo.getPhoneNumber());
        entity.setCoin(1);
        entity.setStage(1);
        entity.setCard1(0);
        entity.setCard2(0);
        entity.setCard3(0);
        entity.setCard4(0);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        folidayGameMapper.insert(entity);
        return entity;
    }

    @Override
    public FolidayGame getUserGameByUserId(String userId) {
        Condition condition = new Condition(FolidayGame.class);
        condition.and().andEqualTo("userId", userId);
        return folidayGameMapper.selectOneByExample(condition);
    }

    @Override
    @Transactional
    public FolidayGame gainCard(Long id, Integer card) {
        FolidayGame entity = getUserGameById(id);
        if (card == null) {
            return entity;
        }
        switch (card) {
            case 1:
                entity.setCard1(entity.getCard1() + 1);
                break;
            case 2:
                entity.setCard2(entity.getCard2() + 1);
                break;
            case 3:
                entity.setCard3(entity.getCard3() + 1);
                break;
            case 4:
                entity.setCard4(entity.getCard4() + 1);
                break;
        }

        if (entity.getStage() == 4) {
            entity.setCoin(entity.getCoin() <= 1 ? 0 : entity.getCoin() - 1);
            entity.setStage(1);
        } else {
            entity.setStage(entity.getStage() + 1);
        }

        entity.setUpdateTime(new Date());

        folidayGameMapper.updateByPrimaryKey(entity);
        return entity;
    }

    @Override
    public FolidayGame getUserGameById(Long id) {
        return folidayGameMapper.selectByPrimaryKey(id);
    }

    @Override
    public void addCoinByUser(Long userGameId, String userId) {
        FolidayGame userGame = getUserGameById(userGameId);
        if (userGame == null || userGame.getUserId().equals(userId)) {
            return;
        }
        FolidayGameCoin coin = getCoin(userGameId, userId);
        if (coin != null) {
            return;
        }
        coin = new FolidayGameCoin();
        coin.setUserGameId(userGameId);
        coin.setUserId(userId);

        folidayGameCoinMapper.insert(coin);

        folidayGameMapper.addCoinByUser(userGameId);
    }

    private FolidayGameCoin getCoin(Long userGameId, String userId) {
        Condition condition = new Condition(FolidayGameCoin.class);
        condition.and().andEqualTo("userGameId", userGameId).andEqualTo("userId", userId);
        List<FolidayGameCoin> datalist = folidayGameCoinMapper.selectByExample(condition);
        if (datalist != null && datalist.size() > 0) {
            return datalist.get(0);
        } else {
            return null;
        }
    }
}
