package com.xingyutang.foliday.service.impl;

import com.xingyutang.app.service.AppGenericService;
import com.xingyutang.foliday.entity.FolidayCardPool;
import com.xingyutang.foliday.entity.FolidayGame;
import com.xingyutang.foliday.entity.FolidayGameAward;
import com.xingyutang.foliday.entity.FolidayGameCoin;
import com.xingyutang.foliday.entity.FolidayPrizePool;
import com.xingyutang.foliday.mapper.FolidayCardPoolMapper;
import com.xingyutang.foliday.mapper.FolidayGameAwardMapper;
import com.xingyutang.foliday.mapper.FolidayGameCoinMapper;
import com.xingyutang.foliday.mapper.FolidayGameMapper;
import com.xingyutang.foliday.mapper.FolidayPrizePoolMapper;
import com.xingyutang.foliday.service.FolidayGameService;
import com.xingyutang.foliday.vo.FolidayUserVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Condition;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FolidayGameServiceImpl implements FolidayGameService {
    private final static int MAX_STAGE = 3;

    @Autowired
    private FolidayGameMapper folidayGameMapper;
    @Autowired
    private FolidayGameCoinMapper folidayGameCoinMapper;
    @Autowired
    private FolidayGameAwardMapper folidayGameAwardMapper;
    @Autowired
    private AppGenericService appGenericService;
    @Autowired
    private FolidayCardPoolMapper cardPoolMapper;
    @Autowired
    private FolidayPrizePoolMapper prizePoolMapper;

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
    public synchronized int gainCard(Long id) {
        FolidayGame entity = getUserGameById(id);

        List<FolidayCardPool> cardPools = getCardPoolList();
        List<FolidayCardPool> availCardPool = cardPools.stream().filter(c -> c.getAmount() > 0).collect(Collectors.toList());

        int randomIndex = RandomUtils.nextInt(0, availCardPool.size());
        FolidayCardPool cardPool = availCardPool.get(randomIndex);
        int card = cardPool.getId();
        cardPool.setAmount(cardPool.getAmount() - 1);

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

        entity.setUpdateTime(new Date());

        folidayGameMapper.updateByPrimaryKey(entity);
        cardPoolMapper.updateByPrimaryKey(cardPool);
        return card;
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

    @Override
    public void updateUserGame(FolidayGame userGame) {
        userGame.setUpdateTime(new Date());
        folidayGameMapper.updateByPrimaryKey(userGame);
    }

    @Override
    @Transactional
    public synchronized void claimAward(FolidayGameAward gameAward) {
        FolidayGame userGame = getUserGameById(gameAward.getUserGameId());

        userGame.setCard1(userGame.getCard1() - gameAward.getCard1());
        if (userGame.getCard1() < 0) {
            throw new IllegalArgumentException("卡片不够");
        }
        userGame.setCard2(userGame.getCard2() - gameAward.getCard2());
        if (userGame.getCard2() < 0) {
            throw new IllegalArgumentException("卡片不够");
        }
        userGame.setCard3(userGame.getCard3() - gameAward.getCard3());
        if (userGame.getCard3() < 0) {
            throw new IllegalArgumentException("卡片不够");
        }
        userGame.setCard4(userGame.getCard4() - gameAward.getCard4());
        if (userGame.getCard4() < 0) {
            throw new IllegalArgumentException("卡片不够");
        }
        gameAward.setCreateTime(new Date());

        Integer awardType = gameAward.getAwardType();
        FolidayPrizePool prizePool = getPrizePoolById(awardType);
        if (prizePool == null) {
            throw new IllegalArgumentException("不存在的奖品类型");
        }
        if (prizePool.getAmount() <= 0) {
            throw new IllegalStateException("奖品已经兑换完毕");
        }
        prizePool.setAmount(prizePool.getAmount() - 1);

        prizePoolMapper.updateByPrimaryKey(prizePool);
        folidayGameMapper.updateByPrimaryKey(userGame);
        folidayGameAwardMapper.insert(gameAward);
    }

    @Override
    public FolidayGameAward getAwardByGameId(Long userGameId) {
        Condition condition = new Condition(FolidayGameAward.class);
        condition.and().andEqualTo("userGameId", userGameId);
        List<FolidayGameAward> dataList = folidayGameAwardMapper.selectByExample(condition);
        if (CollectionUtils.isNotEmpty(dataList)) {
            return dataList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<FolidayGame> listAllUserGames() {
        return folidayGameMapper.selectAll();
    }

    @Override
    public InputStream exportAll() throws IOException {
        List<FolidayGame> dataList = listAllUserGames();

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            int rowIndex = 0;
            int colIndex = 0;
            XSSFRow titleRow = sheet.createRow(rowIndex++);

            titleRow.createCell(colIndex++).setCellValue("序号");
            titleRow.createCell(colIndex++).setCellValue("姓名");
            titleRow.createCell(colIndex++).setCellValue("电话");
            titleRow.createCell(colIndex++).setCellValue("创建时间");

            XSSFRow row;
            for (int i = 0; i < dataList.size(); i++) {
                row = sheet.createRow(rowIndex++);
                FolidayGame item = dataList.get(i);
                int j = 0;
                row.createCell(j++).setCellValue(String.valueOf(i + 1));
                row.createCell(j++).setCellValue(item.getName());
                row.createCell(j++).setCellValue(item.getPhoneNumber());
                row.createCell(j++).setCellValue(DateFormatUtils.format(item.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            }
            return appGenericService.exportAsInputStream(wb);
        }
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

    @Override
    public List<FolidayPrizePool> getPrizePoolList() {
        return prizePoolMapper.selectAll();
    }

    @Override
    public FolidayPrizePool getPrizePoolById(Integer id) {
        return prizePoolMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<FolidayCardPool> getCardPoolList() {
        return cardPoolMapper.selectAll();
    }
}
